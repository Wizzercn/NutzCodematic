package pandy.security;

/**
 * Created by IntelliJ IDEA.
 * User: Admin12
 * Date: 2005-11-18
 * Time: 9:01:12
 * To change this template use File | Settings | File Templates.
 */
public class Decode {
	public static String Decrypt(String str)
	{
		String encoderText="lsdfoglkwjemc-091324jlkmsda-0sd=1234;l;lsdkOPIER203-4LKJSLDJAS0D925JKNNC,MANSLDJQ32ELK1N4SAIp089er0234lkjo9df82l3kjlknf,nzxc,mn;lasdj9wquelq;d]qowe[;wq;qkwellsdkfj0-0POPOAR0W8RPOp-02@#$sdklj$#)0asdlksadLKJFA9820934)(&$3ij09sdj34-sdfj2po345-09dlkfjlkv,mxncv;laskdkl/a;au093hakjh2389~!@%&*%#&^539478(*&)^(&^_*8-*_+++|78w3ihsdnmnclksdj)(*#%*_@$(+#@$)&@#^*&^#@$()(*#@$HDFIkdhfgkjh098k;ldsk.sdv.c,msd;flkp0w34;2lk-=sd0p121o39-werl2k3;4lj09sdflskjlekfj,mv,mcxvjlksjdflksjdl*(#@!&akhduyqweperilmmdxcasnd*(#@9879327kjhasudfewr kwehriwueyrhc ausdgiq7w8e71 cdsh93ol2q32879y8932qwhdkjanhdskjaoe*&w#jh$)(*dsFshc na89wue32e981yewher12(*&#quds)(*i3o1928osaihdaklsdkalkduqowe3290874kljhklasdhlijhqweio4hwe89(*$#$eriho349oij(#*q$OIJHO)(&*#$_)(IUDSOIUoiOIUSAODFU034liusdrogiuet0lsdmc,.mg;lq-091lk3l;kjsdf--123098fe*(JOKJSFD983345oihjdp0(#*$&#@!HKJH!(@#*&ioysdk@#)uhOA7E98R7239845K(*&(#@*$&HKFDJHWERYIWoi)(*&#@&^%@!dsfoi;.;,p[osklejr230897*(&we2&^%@78*(&#@!(7~&*~^@*&^#(*&auroiqkjwrhoasdf89qlrlkjpour09werk23jh";
		int seed, pre;
		try{
		seed = DecodeChar(str,0);
		} catch (NumberFormatException e)
		{
			return "";
		}
		try{
	    pre = DecodeChar(str,2);
		}catch (NumberFormatException e)
		{
			return "";
		}

	    pre = pre&3;

	    String ret = "";
	    int x;
	    int i,j;
	    int len = str.length();
	    int elen = encoderText.length();


		for (i=pre+pre+4,j=seed;i<len;i+=2){
			try{
				x = DecodeChar(str,i);

			}catch(NumberFormatException e)
			{
			return "";
			}

			int intValue= (int) encoderText.charAt(j);
			x ^= intValue;
			char ch=(char) x;
	        ret += ch;
			if (++j>=elen)
				j=0;
	    }

	    return ret;
	}

	public static String Encrypt(String str)
	{
		String encoderText="lsdfoglkwjemc-091324jlkmsda-0sd=1234;l;lsdkOPIER203-4LKJSLDJAS0D925JKNNC,MANSLDJQ32ELK1N4SAIp089er0234lkjo9df82l3kjlknf,nzxc,mn;lasdj9wquelq;d]qowe[;wq;qkwellsdkfj0-0POPOAR0W8RPOp-02@#$sdklj$#)0asdlksadLKJFA9820934)(&$3ij09sdj34-sdfj2po345-09dlkfjlkv,mxncv;laskdkl/a;au093hakjh2389~!@%&*%#&^539478(*&)^(&^_*8-*_+++|78w3ihsdnmnclksdj)(*#%*_@$(+#@$)&@#^*&^#@$()(*#@$HDFIkdhfgkjh098k;ldsk.sdv.c,msd;flkp0w34;2lk-=sd0p121o39-werl2k3;4lj09sdflskjlekfj,mv,mcxvjlksjdflksjdl*(#@!&akhduyqweperilmmdxcasnd*(#@9879327kjhasudfewr kwehriwueyrhc ausdgiq7w8e71 cdsh93ol2q32879y8932qwhdkjanhdskjaoe*&w#jh$)(*dsFshc na89wue32e981yewher12(*&#quds)(*i3o1928osaihdaklsdkalkduqowe3290874kljhklasdhlijhqweio4hwe89(*$#$eriho349oij(#*q$OIJHO)(&*#$_)(IUDSOIUoiOIUSAODFU034liusdrogiuet0lsdmc,.mg;lq-091lk3l;kjsdf--123098fe*(JOKJSFD983345oihjdp0(#*$&#@!HKJH!(@#*&ioysdk@#)uhOA7E98R7239845K(*&(#@*$&HKFDJHWERYIWoi)(*&#@&^%@!dsfoi;.;,p[osklejr230897*(&we2&^%@78*(&#@!(7~&*~^@*&^#(*&auroiqkjwrhoasdf89qlrlkjpour09werk23jh";

		int seed = (int)Random(255);
		int pre = seed & 3;
		int len = str.length();
		int elen = encoderText.length();
		int i,j;

		String ret = "";
		ret += EncodeChar(seed);
		ret += EncodeChar((((int)Random(255))&0xfc)+pre);
		for (i=0;i<pre;i++)
		    ret += EncodeChar((int)Random(255));

		for (i=0,j=seed;i<len;i++){
		    ret += EncodeChar( ((int)str.charAt(i)) ^ ((int)encoderText.charAt(j)) );
		if (++j>=elen)
		    j=0;
		}

		//System.out.println("enc: "+ret);
		return ret;
	}

	public static double Random(int x)
	{
		return (Math.floor(Math.random()*1000000))%x;
	}

	public static String EncodeChar(int c)
	{
	    String s = "";
		double x = Math.floor(c/16);
	    if (x>9){
	        s += (char)(((int)x)-10+0x61);
	    }
	    else{
	        s += (int)x;
	    }
	    x = c%16;
	    if (x>9){
	        s += (char)( ((int)x) -10+0x61);
	    }
	    else{
	        s += (int)x;
	    }
	    return s;
	}

	public static int DecodeChar(String str,int i) throws NumberFormatException
	{
		return Integer.parseInt(str.substring(i,i+2),16);
	}

	static public void main(String[] args)
	{
		//System.out.println("Password is " + Decode.Encrypt("pass"));
		//System.out.println("Password is " + Decode.Decrypt("202000000000"));
	}
}
