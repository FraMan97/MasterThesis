pragma solidity ^0.5.12;

import "EllipticCurve.sol";

contract TimeLockEncryptionContract{
    
    uint256 private counter;
	uint256 private pKeyX;
	uint256 private pKeyY;
	bool private processCompleted;
	bool private puzzleCompleted;
	
    uint256 public constant GX = 0;     //coordinata x del generatore G
	uint256 public constant GY = 2;    //coordinata y del generatore G
	uint256 public constant A = 1;    //coefficiente a della curva
	uint256 public constant B = 4;    //coefficiente b della curva
	uint256 public constant P = 23;    //numero primo del campo finito
	uint256 public constant OG = 29;    //ordine del punto G della curva
	uint256 public constant REWARD = 1000; //premio per aver trovato la chiave privata corretta
	uint256 public constant NPARTECIPANTS = 2;
	
	uint256 public sKey;
	uint256[] public seeds;
    address[] public partecipants;
    address public puzzleWinner;

    event log(uint msg);
	
	constructor() public payable{
	    partecipants = new address[](NPARTECIPANTS);
	    seeds = new uint256[](NPARTECIPANTS);
	    counter = 0;
	    sKey = 0;
	    processCompleted = false;
	    puzzleCompleted = false;
	}
	
	
	function mod_inv(uint256 _a, uint256 _b) private returns (uint256){
	    uint256 temp = _a % _b;
	    for(uint256 i = 1; i < _b; i++){
	        if ((temp * i) % _b == 1){
	            return i;
	        }
	    }
	    return 1;
	}
	
	function right_exp_mod(uint256 _base, uint256 _exp, uint256 _mod) private returns (uint256){
	    uint256 result = 1;
        _base = _base % _mod;
        while (_exp > 0){
            if (_exp % 2 == 1)
                result = (result * _base) % _mod;
            _exp = _exp >> 1;
            _base = (_base * _base) % _mod;
        }
        return result;
	}
	
	function icart_function(uint256 _seed) private returns (uint256, uint256){
	    uint256 exp = ((((P * 2) - 1)) * (mod_inv(3, P)) % P);
	    int256 v = (int(int(A * 3) - int(_seed ** 4)) * int(mod_inv(6 *_seed, P)));
	    uint256 x = ( uint(v * v) - B - ( (_seed ** 6) * mod_inv(27, P) ) );
	    x = right_exp_mod(x, exp, P);
	    x = ( x + ( (_seed ** 2) * mod_inv(3, P) ))  % P;
	    uint256 y = uint((((int(_seed * x) + v) % int(P)) + int(P)) % int(P));
	    return (x, y);
	    
	}
	
	function sum_seeds() private returns (uint256){
	    uint256 sum = 0;
	    for(uint256 i = 0; i < NPARTECIPANTS; i++){
	        sum += seeds[i];
	    }
	    return sum;		    
	}
	
	function get_point_on_curve() private returns (uint256, uint256){
	    if (processCompleted == true)
	    {
	        (pKeyX, pKeyY) = icart_function(sum_seeds() % P);
	        return (pKeyX, pKeyY);
	    }
	        return (0,0);
	}

	function insert_new_seed(uint256 _seed) public{
	    if (processCompleted == false){
        	seeds[counter] = _seed;
    		partecipants[counter] = msg.sender;
    		counter++;
    		if (counter == NPARTECIPANTS){
    		    processCompleted = true;
    		}
	    }
	}
	
	function insert_candidate_private_key(uint256 _cPKey) public returns (bool){
	    if ((puzzleCompleted == false) && (processCompleted == true))
	    {
    	    (uint256 qX, uint256 qY) = EllipticCurve.ecMul(
              _cPKey,
              GX,
              GY,
              A,
              P
            );
            if ((qX == pKeyX) && (qY == pKeyY)){
                sKey = _cPKey;
                puzzleCompleted = true;
                puzzleWinner = msg.sender;
                transfer_reward(REWARD, msg.sender);
                return true;
            }
	    }
	    return false;
	}
	

	
	function transfer_reward(uint256 _amount, address payable _winner)  private{
	    _winner.transfer(_amount);
	} 
	
	function get_public_key() public returns (uint256, uint256){
		 return get_point_on_curve();
	}
	
	function get_private_key() public returns (uint256){
	    return sKey;
	}
	
	function get_seeds() private returns (uint256){
		    return sum_seeds();
	}
}