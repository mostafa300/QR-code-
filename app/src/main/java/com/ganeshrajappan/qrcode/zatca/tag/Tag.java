package com.ganeshrajappan.qrcode.zatca.tag;

public class Tag {
	
	private int tag;
	private String value;
	
	public Tag(int tag, String value) {
		if (value == null || value.trim().equals("")) {
			throw new IllegalArgumentException("Value cannot be null or empty");
		}
		this.tag = tag;
		this.value = value;
	}
	
	private int getTag() {
		return this.tag;
	}
	
	private String getValue() {
		return this.value;
	}
	
	private int getLength() {
		return this.value.getBytes().length;
	}
	
	private String toHex(int value) {
		String hex = String.format("%02X", value);
		String input = hex.length() % 2 == 0 ? hex : hex  + "0";
	    StringBuilder output = new StringBuilder();
	    for (int i = 0; i < input.length(); i+=2) {
	        String str = input.substring(i, i+2);
	        output.append((char)Integer.parseInt(str, 16));
	    }
	    return output.toString();
	}
	
	@Override
	public String toString() {
		return this.toHex(this.getTag()) + this.toHex(this.getLength()) + (this.getValue());
	}

}
