package kit.route.a.lot.common.UTMTester;

class Tester{

public static void main(String[]arg){
/*Test von http://www.kompf.de/gps/distcalc.html*/
	WeightCalculatorTester wCalc = WeightCalculatorTester.getInstance();
	double entf;
	/*Test1*/
	entf = wCalc.calcWeightWithUTM(	49.9917, 8.41321, 50.0049, 8.42182);
	System.out.println("von Rüsselsheim Bahnhof nach Rüsselsheim Opelbrücke: "+ entf);
	/*Test2*/
	entf = wCalc.calcWeightWithUTM(52.5164, 13.3777, 38.692668, -9.177944);
        System.out.println("Berlin Brandenburger Tor nach Lissabon Tejo Brücke: " + entf);
}

}//end class
