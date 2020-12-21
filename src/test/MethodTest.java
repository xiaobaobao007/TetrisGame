package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 测试类
 * 方法映射
 */
class Calculator {

	public static double mul(double score1, double score2) {
		return score1 * score2;
	}

	public double add(double score1, double score2) {
		return score1 + score2;
	}

	public void print() {
		System.out.println("OK");
	}

	public void print1(int a, int b) {
		System.out.println(a + b);
		print2();
	}

	public void print2() {
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			System.out.println(element.getMethodName());
		}
		System.out.println("OK");
	}

	public void print3() {
		System.out.println("OK");
	}
}


public class MethodTest {
	MethodTest() {
//        Map<Integer, Method> map = new HashMap<>();
//        try {
//            map.put(1, new Calculator().getClass().getMethod("print1"));
//            map.put(2, new Calculator().getClass().getMethod("print2"));
//            map.put(3, new Calculator().getClass().getMethod("print3"));
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
//        Method method = map.get(1);
//        Object invoke = null;
		try {
			Calculator.class.getMethod("print1", new Class[]{int.class, int.class}).invoke(new Calculator(), 1, 2);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//        new Timer().schedule(new TimerTask() {
//            public void run() {
//                System.out.println("11232");
//            }
//        }, 0 , 1);
		new MethodTest();
	}

	public void other() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Class<Calculator> clz = Calculator.class;

		//或者通过类的完整路径获取,这个方法由于不能确定传入的路径是否正确,这个方法会抛ClassNotFoundException
//      Class<Calculator> clz = Class.forName("MethodTest.Calculator");

		//或者new一个实例,然后通过实例的getClass()方法获取
//      Calculator s = new Calculator();
//      Class<Calculator> clz = s.getClass();

		//1. 获取类中带有方法签名的mul方法,getMethod第一个参数为方法名,第二个参数为mul的参数类型数组
		Method method = clz.getMethod("mul", double.class, double.class);

		//invoke 方法的第一个参数是被调用的对象,这里是静态方法故为null,第二个参数为给将被调用的方法传入的参数
		Object result = method.invoke(null, 2.0, 2.5);

		//如果方法mul是私有的private方法,按照上面的方法去调用则会产生异常NoSuchMethodException,这时必须改变其访问属性
		//method.setAccessible(true);//私有的方法通过发射可以修改其访问权限
		System.out.println(result);//结果为5.0
		//2. 获取类中的非静态方法
		Method method_2 = clz.getMethod("add", double.class, double.class);
		//这是实例方法必须在一个对象上执行
		Object result_2 = method_2.invoke(new Calculator(), 2.0, 2.5);
		System.out.println(result_2);//4.5
		//3. 获取没有方法签名的方法print
		Method method_3 = clz.getMethod("print");
		Object result_3 = method_3.invoke(new Calculator(), (Object) null);//result_3为null,该方法不返回结果
	}
}
