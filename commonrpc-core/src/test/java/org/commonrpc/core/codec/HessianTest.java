package org.commonrpc.core.codec;

import com.cross.plateform.common.rpc.core.codec.impl.HessianDecoder;
import com.cross.plateform.common.rpc.core.codec.impl.HessianEncoder;
import org.commonrpc.core.vo.Student2;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by lejing on 15/11/4.
 */
public class HessianTest {
	@Test
	public void test1() throws Exception {
		HessianEncoder hessianEncoder = new HessianEncoder();
		HessianDecoder hessianDecoder = new HessianDecoder();

		Student2 student = new Student2("zhangsan", "man", 0);
		Student2 student1 = new Student2("stu1", "man", 1);
		student.setStudent1(student1);
		student.setStudent2(student1);
		byte[] encode = hessianEncoder.encode(student);

		Object decode = hessianDecoder.decode(null, encode);
		System.out.println(decode);

		Student2 s = (Student2) decode;
		System.out.println(s.getName() + "," + s.getSex());
		System.out.println(s.getStudent1() + "----" + s.getStudent2());
		Assert.assertEquals(s.getStudent1(), s.getStudent2());
	}
}
