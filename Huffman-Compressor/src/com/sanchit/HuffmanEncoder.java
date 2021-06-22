package com.sanchit;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class HuffmanEncoder {

	private static int ALPHABET_SIZE = 256;
    private static ArrayList<String> dataInString;
    private static int bytes[] = {0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff,
			0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff,
			0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };
    static class HuffmanEncodedResult {
		String encodedData;
		Node root;

		HuffmanEncodedResult(String encodedData, Node root) {
			this.encodedData = encodedData;
			this.root = root;
		}

		public Node getRoot() {
			return root;
		}
	}

	static class Node {
		char character;
		int frequency;
		Node left, right;

		Node(char character, int frequency, Node left, Node right) {
			this.character = character;
			this.frequency = frequency;
			this.left = left;
			this.right = right;
		}
	}

	public static HuffmanEncodedResult compress(String data) {
		int freq[] = buildFrequencyTable(data);
		Node root = buildHuffmanTree(freq);
		HashMap<Character, String> lookupTable = buildLookupTable(root);
		return new HuffmanEncodedResult(generateEncodedData(data, lookupTable), root);
	}

	public static String generateEncodedData(String data, HashMap<Character, String> lookupTable) {
		StringBuilder builder = new StringBuilder();
		for (char character : data.toCharArray()) {
			builder.append(lookupTable.get(character)+"\n");
			dataInString.add(lookupTable.get(character));
		}

		return builder.toString();
	}

	public static int[] buildFrequencyTable(String data) {
		int[] freq = new int[ALPHABET_SIZE];

		for (char character : data.toCharArray())
			freq[character]++;

		return freq;
	}

	public static Node buildHuffmanTree(int freq[]) {
		// for string = "abcffg" pq has a,b,c,g,f
		// '\0' is smaller than alphabets
		PriorityQueue<Node> pq = new PriorityQueue<>(
				(n1, n2) -> (n1.frequency == n2.frequency) ? Integer.compare(n1.character, n2.character)
						: Integer.compare(n1.frequency, n2.frequency));

		// need to use pq as shifting of nodes is needed after comparable, so can't use
		// queue
		// PriorityQueue<Node> pq = new PriorityQueue<>();

		for (char i = 0; i < ALPHABET_SIZE; i++) {
			if (freq[i] > 0)
				pq.add(new Node(i, freq[i], null, null));
		}

		if (pq.size() == 1)
			pq.add(new Node('\0', pq.poll().frequency, null, null));

		while (pq.size() > 1) {
			Node left = pq.poll();
			Node right = pq.poll();
			Node parent = new Node('\0', left.frequency + right.frequency, left, right);
			pq.add(parent);
		}

		return pq.poll();
	}

	public static HashMap<Character, String> buildLookupTable(Node root) {
		HashMap<Character, String> lookupTable = new HashMap<>();
		buildLookupTableHelper(root, "", lookupTable);
		return lookupTable;
	}

	public static void buildLookupTableHelper(Node root, String s, HashMap<Character, String> lookupTable) {

		if (root.left == null && root.right == null) {
			lookupTable.put(root.character, s);
			return;
		}

		buildLookupTableHelper(root.left, s + '0', lookupTable);
		buildLookupTableHelper(root.right, s + '1', lookupTable);
	}

	
	public static void main(String[] args) {

		String fileName = "/Users/sanchit/git/Huffman-Compressor/Huffman-Compressor/src/com/sanchit/compressed_file.txt";
		dataInString = new ArrayList<>();
		try {
			FileOutputStream fileOs = new FileOutputStream(fileName); 
			ObjectOutputStream os = new ObjectOutputStream(fileOs);

			BufferedReader br = new BufferedReader(new FileReader(
					"/Users/sanchit/git/Huffman-Compressor/Huffman-Compressor/src/com/sanchit/input_file.txt"));
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"/Users/sanchit/git/Huffman-Compressor/Huffman-Compressor/src/com/sanchit/encoded_data_file.txt"));

			String s;
			StringBuilder data = new StringBuilder();
			while ((s = br.readLine()) != null)
				data.append(s);

			if (data.length() == 0) {
				System.out.println("Input File Empty");
			} else {
				HuffmanEncodedResult huffmanEncodedResult = compress(data.toString());
				String decompressedData = HuffmanDecoder.decompress(huffmanEncodedResult);
				for(String d: dataInString) {
					int n = Integer.parseInt(d, 2);
					// System.out.println(n);
					// 5&0x3 gives lower 2 bits of 5 i.e 3
					os.writeByte(n&bytes[d.length()]);
				}
				bw.write(huffmanEncodedResult.encodedData);
			}

			bw.close();
			br.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println(n.frequency);
		// System.out.println(n.left.character);
		// System.out.println(n.right.frequency);
		// System.out.println(n.right.left.left.character);
		// System.out.println(n.right.left.right.character);
		// System.out.println(n.right.right.left.character);
		// System.out.println(n.right.right.right.character);
	}
}