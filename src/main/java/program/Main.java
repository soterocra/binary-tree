package program;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {

	private static boolean headerPrinted = false;
	
	private static final String MERMAID_URL = "https://mermaid-js.github.io/mermaid-live-editor/";
	private static final String MERMAID_JS_TYPE = "graph TD;";

	private static List<Integer> numbersListLeft = new ArrayList<>();
	private static List<Integer> numbersListRigth = new ArrayList<>();

	private static Integer rootMember = null;

	public static void main(String[] args) {

		new Random().ints(20, 1, 20).forEach(number -> {
			if (rootMember == null) {
				rootMember = number;
			}

			if (number < rootMember && !numbersListLeft.contains(number)) {
				numbersListLeft.add(number);
			} else if (number > rootMember && !numbersListRigth.contains(number)) {
				numbersListRigth.add(number);
			}
		});

		// mock
//		rootMember = 8;
//		numbersListLeft = new ArrayList<>(Arrays.asList(3, 1, 6, 4, 7));		
//		numbersListRigth = new ArrayList<>(Arrays.asList(10, 14, 13));
		
		ArrayList<Integer> numbersListLeftCopy = new ArrayList<>();
		ArrayList<Integer> numbersListRigthCopy = new ArrayList<>();
		
		numbersListLeft.forEach(numbersListLeftCopy::add);
		numbersListRigth.forEach(numbersListRigthCopy::add);
		
		LinkedHashMap<String, Integer> downToTopDataLeft = new LinkedHashMap<>();		
		LinkedHashMap<String, Integer> downToTopDataRigth = new LinkedHashMap<>();		
		
		generateTreeDownToUp(new ArrayList<>(numbersListLeft), downToTopDataLeft);
		generateTreeDownToUp(new ArrayList<>(numbersListRigth), downToTopDataRigth);

		LinkedHashMap<String, Integer> downToTopData = new LinkedHashMap<>();
		downToTopData.putAll(downToTopDataLeft);
		downToTopData.putAll(downToTopDataRigth);
		
		printResultUpToDown(downToTopDataLeft, true);		
		printResultUpToDown(downToTopDataRigth, true);
		System.out.println("Altura:" + altura(downToTopDataLeft, downToTopDataRigth));		
		
		System.out.printf("%n%n%n%n%nPOSITIVOS APENAS, FOLHA PARA O TOPO%n%n");
		System.out.println("graph TD;");
		printResultUpToDown(positivos(downToTopDataLeft), true);		
		printResultUpToDown(positivos(downToTopDataRigth), true);
		
		headerPrinted = false;
		
		LinkedHashMap<String, Integer> topToDownDataLeft = new LinkedHashMap<>();		
		LinkedHashMap<String, Integer> topToDownDataRigth = new LinkedHashMap<>();	
		
		generateTreeUpToDown(new ArrayList<>(numbersListLeft), topToDownDataLeft);
		generateTreeUpToDown(new ArrayList<>(numbersListRigth), topToDownDataRigth);

		LinkedHashMap<String, Integer> topToDownData = new LinkedHashMap<>();
		topToDownData.putAll(topToDownDataLeft);
		topToDownData.putAll(topToDownDataRigth);
		
		printResultUpToDown(topToDownDataLeft, false);		
		printResultUpToDown(topToDownDataRigth, false);		
		System.out.println("Altura:" + altura(topToDownDataLeft, topToDownDataRigth));
		
	}
	
	public static void generateTreeDownToUp(List<Integer> list, LinkedHashMap<String, Integer> destiny) {

		if (list.isEmpty()) {
//			printResultUpToDown(destiny, true);
			return;
		}
		
		if (list.size() >= 3) {
			Integer antepenultimate = list.size() - 3;
			Integer penultimate = list.size() - 2;
			Integer last = list.size() - 1;
			
			if (list.get(last) < list.get(antepenultimate) && list.get(penultimate) < list.get(antepenultimate) ||
					list.get(last) > list.get(antepenultimate) && list.get(penultimate) > list.get(antepenultimate)) {
				destiny.put("a" + list.get(penultimate), list.get(last));								
				destiny.put("a" + list.get(antepenultimate), list.get(penultimate));								
			} else if (list.get(last) < list.get(penultimate)) {
				destiny.put("a" + list.get(antepenultimate), list.get(last));				
				destiny.put("b" + list.get(antepenultimate), list.get(penultimate));				
			} else {
				destiny.put("a" + list.get(antepenultimate), list.get(penultimate));		
				destiny.put("b" + list.get(antepenultimate), list.get(last));				
			}
						
			list.remove(list.size() - 1);
			list.remove(list.size() - 1);
			
			if (list.size() == 1)
				list.remove(0);
			
		} else if (list.size() == 2) {
			destiny.put("a" + list.get(0), list.get(1));
			list.remove(0);
			list.remove(0);
		} else if (list.size() == 1) {
			destiny.put("a" + list.get(0), null);
			list.remove(0);
		}
		
		generateTreeDownToUp(list, destiny);
	
	}
	
	public static void generateTreeUpToDown(List<Integer> list, LinkedHashMap<String, Integer> destiny) {

		if (list.isEmpty()) {
//			printResultUpToDown(destiny, false);
			return;
		}

		Integer correctionFactor = 1;

		if (!destiny.isEmpty()) {
			while (correctionFactor < list.size() && destiny.containsValue(list.get(correctionFactor))) {
				++correctionFactor;
			}
		}
		
		Integer parentOf = 2;
		
		if (destiny.containsKey("a" + list.get(0)))
			--parentOf;
		
		if (destiny.containsKey("b" + list.get(0)))
			--parentOf;

		if (parentOf == 0) {
			list.remove(0);
			generateTreeUpToDown(list, destiny);
		} else if (parentOf == 1 && correctionFactor <= list.size() - 2) {
			destiny.put("b" + list.get(0), list.get(correctionFactor));
			list.remove(0);
			generateTreeUpToDown(list, destiny);
		} else {
			if (correctionFactor <= list.size() - 2) {
				if (list.get(correctionFactor) > list.get(0) && list.get(correctionFactor + 1) > list.get(0)
						|| list.get(correctionFactor) < list.get(0) && list.get(correctionFactor + 1) < list.get(0)) {
					destiny.put("a" + list.get(0), list.get(correctionFactor));
					destiny.put("a" + list.get(correctionFactor), list.get(correctionFactor + 1));
				} else {
					if (list.get(0) > list.get(correctionFactor)) {
						destiny.put("a" + list.get(0), list.get(correctionFactor));
						destiny.put("b" + list.get(0), list.get(correctionFactor + 1));
					} else if (list.get(0) > list.get(correctionFactor + 1)) {
						destiny.put("a" + list.get(0), list.get(correctionFactor + 1));
						destiny.put("b" + list.get(0), list.get(correctionFactor));
					}
				}
				
				list.remove(0);
				generateTreeUpToDown(list, destiny);
			} else if (list.size() > 1 && correctionFactor == list.size() - 1) {
				destiny.put("a" + list.get(0), list.get(correctionFactor));
//				printResultUpToDown(destiny, false);
			} else {
//				printResultUpToDown(destiny, false);
			}			
		}
	}

	public static void printResultUpToDown(LinkedHashMap<String, Integer> map, boolean downToUp) {
		
		boolean printed = false;
		Integer skipCount = 0;
		
		if (downToUp) {
			skipCount = map.entrySet().size() - 1;
		}
		if (!headerPrinted) {
			headerPrinted = true;
			System.out.println("\n====================================================================================\n");
			if (downToUp) {
				System.out.println("Das folhas para raiz...");				
			}
			else {
				System.out.println("Da raiz para as folhas...");
			}
			System.out.println("Resultado abaixo, para ver graficamente cole o código no site:\n\n" + MERMAID_URL);
			System.out.println();			
			System.out.println(MERMAID_JS_TYPE);
		}
		if (!map.isEmpty()) {
			System.out.printf("    %s-->%s;%n", rootMember, map.entrySet().stream().skip(skipCount).findFirst().get().getKey().toString().replaceAll("[a-b]", ""));			
			map.forEach((k, v) -> {
				if (v != null) {
					System.out.printf("    %s-->%s;%n", k.toString().replaceAll("[a-b]", ""), v.toString());
				}
			});
		}
	}
	
	public static Long altura(LinkedHashMap<String, Integer> mapLeft, LinkedHashMap<String, Integer> mapRigth) {
		Long countLeft = 0L;
		for (Map.Entry<String, Integer> entry : mapLeft.entrySet()) {
			if (entry.getKey().startsWith("a")) {
				++countLeft;				
			}
	    }
		Long countRigth = 0L;
		for (Map.Entry<String, Integer> entry : mapRigth.entrySet()) {
			if (entry.getKey().startsWith("a")) {
				++countRigth;				
			}
	    }
		
		return (countLeft > countRigth ? countLeft : countRigth) + 2;
	}
	
	public static LinkedHashMap<String, Integer> positivos(LinkedHashMap<String, Integer> map) {
		Map<? extends String, ? extends Integer> collect = map.entrySet().stream()
				.filter(x -> Integer.parseInt(x.getKey().replaceAll("[a-b]", "")) % 2 == 0)
				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		
		LinkedHashMap<String, Integer> newMap = new LinkedHashMap<>();
		newMap.putAll(collect);
		return newMap;
		
	}
}
