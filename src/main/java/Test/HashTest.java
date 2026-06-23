package Test;

import model.Hash;

public class HashTest {

	public static void main(String[] args) {
		Hash hash = new Hash();
		//String hash256 = hash.getSHA256(null);
		String hash512 = hash.getSHA512(null);
		//System.out.println("hash256:" + hash256 + "文字数" + hash256.length());
		System.out.println("hash512:" + hash512 + "文字数" + hash512.length());
	}

}
