package ahs.lost.toy;

public class Sum {
	public static void main(String[] args) {
		int $v = 0;
		for (String $arg : args)
			$v += Integer.parseInt($arg);
		System.out.println($v);
	}
	
	public static int sum(int... $arr) {
		int $v = 0;
		for (int $i : $arr) $v += $i;
		return $v;
	}
}
