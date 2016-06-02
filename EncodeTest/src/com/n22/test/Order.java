package com.n22.test;

public class Order extends BaseBean {

	private static final long serialVersionUID = 1L;
	public String[] arr = new String[]{"11","22","33"};
	//类属性不会被转换到json字符串中
	public static String[] arr2 = new String[]{"11","22","33"};

	public String name;
	public String id;
	public Benefit benefit;
	public Holder holder1 = new Holder("man1");
	
	static class Holder{
		public static int id = 1;
		public int no;
		public String man;
		public String woman;
		public boolean isTrue;
		public Holder(String man) {
			this.man = man;
		}
	}
}
