package com.sanchit;

import com.sanchit.HuffmanEncoder.HuffmanEncodedResult;
import com.sanchit.HuffmanEncoder.Node;

public class HuffmanDecoder {

	public static String decompress(HuffmanEncodedResult result) {

		StringBuilder originalData = new StringBuilder();
		Node root = result.getRoot(), cur = root;

		int ptr = 0;
		while(ptr < result.encodedData.length()) {
			while(cur.left != null && cur.right != null) {
				char bit = result.encodedData.charAt(ptr);
				if(bit == '1') {
					cur = cur.right;
				}
				else {
					cur = cur.left;
				}
				ptr++;
			}

			originalData.append(cur.character);
			cur = root;
		}

		return originalData.toString();
	}
}
