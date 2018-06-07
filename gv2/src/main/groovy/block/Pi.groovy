package block

class Pi {
	def calculate(precision) {
		return 3+4* sequenceWith(precision);
	}
    def sequenceWith(precision){
    
        double seriesResult = 0;
        def  sign
        def i
        def x
		for( i = 0; i < precision; i++) {
			sign = Math.pow(-1, i%2);
			x = (i+1)*2;
			seriesResult += sign*(1/ (x*(x+1)*(x+2)));
			}
		return seriesResult;
	}
}
