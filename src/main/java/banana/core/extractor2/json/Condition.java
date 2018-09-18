package banana.core.extractor2.json;

import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;

public class Condition {

	public static final String SYMBOL_GT = ">";
	public static final String SYMBOL_LT = "<";
	public static final String SYMBOL_EQ = "=";
	public static final String SYMBOL_NE = "!=";
	public static final String SYMBOL_AND = "&&";
	public static final String SYMBOL_OR = "||";
	
	private String symbol;
	
	private Object left;
	
	private Object right;
	
	//(id='1001') (id > 1001) (id='1001' && num < 3) (age<10 || (name='enny' && gender='f')) ((name='enny' && gender='f') && age<10)
	public Condition(String express) {
		String leftRight[] = extractLeftRight(express);
		String leftExpress = leftRight[0];
		String rightExpress = leftRight[1];
		if (leftExpress.contains("&&") || leftExpress.contains("||") || leftExpress.contains(">") || leftExpress.contains("<") || leftExpress.contains("=") || leftExpress.contains("!=")) {
			left = new Condition(leftExpress);
		}else {
			left = leftExpress;
		}
		if (rightExpress.contains("&&") || rightExpress.contains("||") || rightExpress.contains(">") || rightExpress.contains("<") || rightExpress.contains("=") || rightExpress.contains("!=")) {
			right = new Condition(rightExpress);
		}else {
			right = rightExpress;
		}
		symbol = leftRight[2];
	}
	
	public Filter getFilter() {
		if (symbol.equals("&&")) {
			Filter leftPredicate = ((Condition)left).getFilter();
			Filter rightPredicate = ((Condition)right).getFilter();
			return leftPredicate.and(rightPredicate);
		}
		
		if (symbol.equals("||")) {
			Filter leftPredicate = ((Condition)left).getFilter();
			Filter rightPredicate = ((Condition)right).getFilter();
			return leftPredicate.or(rightPredicate);
		}
		
		Criteria criteria = where((String)left);
		if (symbol.equals(">")) {
			criteria.gt(Integer.parseInt((String) right));
		}else if (symbol.equals("<")) {
			criteria.lt(Integer.parseInt((String) right));
		}else if (symbol.equals("=")) {
			if (((String) right).startsWith("'")) {
				criteria.is(right.toString().substring(1,right.toString().length()-1));
			} else {
				criteria.is(Integer.parseInt((String) right));
			}
		}else if (symbol.equals("!=")) {
			if (((String) right).startsWith("'")) {
				criteria.ne(right.toString().substring(1,right.toString().length()-1));
			} else {
				criteria.ne(Integer.parseInt((String) right));
			}
		}
		return filter(criteria);
	}
	
	private boolean bracketBalance(String input) {
		int leftBrackeNumber = 0;
		for (int i = 0; i < input.length(); i++) {
			char cha = input.charAt(i);
			if (cha == '(') {
				leftBrackeNumber++;
			}
			if (cha == ')') {
				leftBrackeNumber--;
			}
		}
		return leftBrackeNumber == 0;
	}
	
	private String[] extractLeftRight(String express) {
		express = express.trim();
		if (express.startsWith("(")) {
			express = express.substring(1, express.length()-1);
		}
		if (express.contains("&&")) {
			int andSymIndex = 0;
			while (true) {
				andSymIndex = express.indexOf("&&",andSymIndex);
				if (andSymIndex == -1) {
					break;
				}
				String left = express.substring(0, andSymIndex).trim();
				String right = express.substring(andSymIndex+2, express.length()).trim();
				if (bracketBalance(left) && bracketBalance(right)) {
					return new String[] {left,right,"&&"};
				}
				andSymIndex+=2;
			}
		}
		
		if (express.contains("||")) {
			int orSymIndex = 0;
			while (true) {
				orSymIndex = express.indexOf("||",orSymIndex);
				if (orSymIndex == -1) {
					break;
				}
				String left = express.substring(0, orSymIndex).trim();
				String right = express.substring(orSymIndex+2, express.length()).trim();
				if (bracketBalance(left) && bracketBalance(right)) {
					return new String[] {left,right,"||"};
				}
				orSymIndex+=2;
			}
		}
		
		if (express.contains(">")) {
			String[] splits = express.split(">");
			return new String[] {splits[0].trim(),splits[1].trim(),">"};
		}
		
		if (express.contains("<")) {
			String[] splits = express.split("<");
			return new String[] {splits[0].trim(),splits[1].trim(),"<"};
		}
		
		if (express.contains("=")) {
			String[] splits = express.split("=");
			return new String[] {splits[0].trim(),splits[1].trim(),"="};
		}
		
		if (express.contains("!=")) {
			String[] splits = express.split("!=");
			return new String[] {splits[0].trim(),splits[1].trim(),"!="};
		}
		return null;
	}
	
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Object getLeft() {
		return left;
	}

	public void setLeft(Object left) {
		this.left = left;
	}

	public Object getRight() {
		return right;
	}

	public void setRight(Object right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "Condition [symbol=" + symbol + ", left=" + left + ", right=" + right + "]";
	}
	
}
