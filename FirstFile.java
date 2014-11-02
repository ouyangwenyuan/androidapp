import "test.Third";
public class FirstFile{		
	public static void main(String[] args){

 		System.out.print("hello,world\n");
		int c = Second.max(5,10);
		System.out.print("5+10="+c+"\n");
		System.out.print("5 and 10 min is "+ Third.min(5,10)+"\r\n");

	}
}
