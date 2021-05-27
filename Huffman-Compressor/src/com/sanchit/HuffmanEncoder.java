package com.sanchit;

import java.util.PriorityQueue;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class HuffmanEncoder {

	private static int ALPHABET_SIZE = 256;

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
		for (char character : data.toCharArray())
			builder.append(lookupTable.get(character));

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

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"/Users/sanchit/eclipse-workspace/Huffman-Compressor/src/com/sanchit/input_file.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					"/Users/sanchit/eclipse-workspace/Huffman-Compressor/src/com/sanchit/output_file.txt"));

			String s;
			StringBuilder data = new StringBuilder();
			while ((s = br.readLine()) != null)
				data.append(s);

			if (data.length() == 0) {
				System.out.println("Input File Empty");
			} else {
				HuffmanEncodedResult huffmanEncodedResult = compress(data.toString());
				String decompressedData = HuffmanDecoder.decompress(huffmanEncodedResult);
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