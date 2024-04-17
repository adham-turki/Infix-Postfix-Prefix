package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Main extends Application {
	public File file; // file to deal with
	TextArea textArea = new TextArea();// text area to show the info
	Cursor cursorStack = new Cursor(); // cursor array that have stacks inside
	private int currentSectionIndex = 0;//to travel between the sections
	private String fileContent;//the file equations

	@Override
	public void start(Stage primaryStage) throws NullPointerException {
		BorderPane root = new BorderPane();// create a border pane
		root.setBackground(getBackground1());// make the background color

		// Create a vertical box to hold the main content
		VBox contentBox = new VBox(20);
		contentBox.setAlignment(Pos.CENTER);

		// Create a horizantal box to hold the buttons
		HBox buttonBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);

		// Create a horizantal box to hold the input content
		HBox inputBox = new HBox(20);
		buttonBox.setAlignment(Pos.CENTER);

		// Add a title text
		Text titleText = new Text("Equation Section");
		titleText.setFont(Font.font("Arial", 30));
		titleText.setFill(Color.GOLD);

		// Add a title text
		Text fileText = new Text("File:");
		fileText.setFont(Font.font("Arial", 30));
		fileText.setFill(Color.GOLD);

		// add a field for the file path
		TextField fileField = new TextField();
		fileField.setPrefHeight(40);
		fileField.setPrefWidth(300);
		fileField.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 15px; ");
		fileField.setEditable(false);

		// add button for loading the file
		Button loadButton = createStyledButton("Load");

		// add a field for the input
		TextField inputField = new TextField();
		inputField.setPrefHeight(40);
		inputField.setPrefWidth(200);
		inputField.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 22px;");

		// combo box to choose between the infix and postfix
		ComboBox<String> optioncomboBox = new ComboBox<>();
		// Set options and styles for the ComboBox
		optioncomboBox.setItems(FXCollections.observableArrayList());
		optioncomboBox.setPromptText("Option");
		optioncomboBox.setStyle(
				"-fx-font-family: Arial; -fx-font-size: 14px; -fx-background-color: #D2E4F6; -fx-border-color: #A6A6A6; -fx-border-radius: 5; -fx-padding: 5px;"
						+ "-fx-effect: innershadow(three-pass-box, rgba(0,0,0,0.3), 2, 0.0, 0, 1);");

		// Add options to the ComboBox
		optioncomboBox.getItems().add("Infix");
		optioncomboBox.getItems().add("PostFix");

		// create the get button to deal with the user input
		Button getButton = createStyledButton("Get");
		textArea.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 22px;");
		textArea.setEditable(false);

		// create the next and previous buttons travel between the sections
		Button nextButton = createStyledButton("Next");
		Button preButton = createStyledButton("Prev");

		// action for the next button
		nextButton.setOnAction(e -> {
			cursorStack.initialization();
			// Increment the current section index
			currentSectionIndex++;
			if (fileContent != null) {
				// Process equations in the new section
				processEquations(fileContent);
			}

		});

		// action for the prev button
		preButton.setOnAction(e -> {
			cursorStack.initialization();
			// Decrement the current section index (if it's greater than 0)
			if (currentSectionIndex > 0) {
				currentSectionIndex--;
				if (fileContent != null) {
					// Process equations in the new section
					processEquations(fileContent);
				}
			}

		});

		// action for the load button
		loadButton.setOnAction(e -> {
			cursorStack.initialization();
			try {
				FileChooser fileChooser = new FileChooser();
				file = fileChooser.showOpenDialog(primaryStage);
				if (file != null) {
					fileField.setText(file.getAbsolutePath());

					// Read file content
					StringBuilder fileContentBuilder = new StringBuilder();//stringbuilder to read the file
					try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						String line;
						while ((line = reader.readLine()) != null) {//loop to check all the lines
							fileContentBuilder.append(line).append("\n");//save the lines in the stringbuilder
						}
					}

					fileContent = fileContentBuilder.toString();

					// Check if the file tags are valid
					if (areTagsValid(fileContent) && areFileTagsBalanced(fileContent)) {
						textArea.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 22px;");
						// Process equations in the first section
						processEquations(fileContent);
					} else {
						textArea.appendText("The file is Not valid");
						textArea.setStyle("-fx-font-weight: bold; -fx-text-fill: red; -fx-font-size: 22px;");
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); // Handle exceptions appropriately
			}
		});

		// action for the get button
		getButton.setOnAction(e -> {
			textArea.clear();
			file = null;
			cursorStack.initialization();//initialize the cursor stack
			if (optioncomboBox.getValue() != null) {//if statement to check if the option is not chosen
				if (optioncomboBox.getValue().equals("Infix")) {//process the infix equations
					textArea.appendText("Infix : \n");
					if (inputField.getText().trim() != "") {
						if (isValidInfix(inputField.getText())) {//check if valid
							String postfix = infixToPostfix(inputField.getText());

							try {
								//print the result in the text area
								textArea.appendText("* " + inputField.getText() + " ==> " + postfix + " ==>"
										+ evaluatePostfix(postfix) + "\n");
							} catch (ArithmeticException e1) {
								//print the error in the text area
								textArea.appendText("* " + inputField.getText());
								textArea.appendText(" ==> Invalid equation  \n");
							}
						} else {
							//print the error in the text area
							textArea.appendText("* " + inputField.getText() + " ==> Invalid equation  \n");
						}
					} else {
						//asking to write something
						textArea.appendText("*   Please fill the field  \n");

					}
				}

				else {
					textArea.appendText("Postfix : \n");
					if (inputField.getText().trim() != "") {
						if (isPostfixExpression(inputField.getText())) {//check if valid
							String prefix = postfixToPrefix(inputField.getText());
							try {
								//print the result in the text area
								textArea.appendText("* " + inputField.getText() + " ==> " + prefix + " ==>"
										+ evaluatePrefix(prefix) + "\n");
							} catch (ArithmeticException e1) {
								//print the error in the text area
								textArea.appendText("* " + inputField.getText());
								textArea.appendText(" ==> Invalid equation  \n");
							}
						} else {
							//print the error in the text area
							textArea.appendText("* " + inputField.getText() + " ==> Invalid equation  \n");
						}
					}
				}
			}
			else {
				//asking the user to choose an option
				textArea.appendText("please choose infix or postfix");
			}
		});

		// add the contents in boxes
		inputBox.getChildren().addAll(fileText, fileField, loadButton, optioncomboBox, inputField, getButton);
		buttonBox.getChildren().addAll(preButton, nextButton);
		// add the boxes in the content box
		contentBox.getChildren().addAll(inputBox, titleText, textArea, buttonBox);

		// add the content box in the center
		root.setCenter(contentBox);

		// Create the scene
		Scene scene = new Scene(root, 900, 600);
		scene.setFill(Color.LIGHTGRAY); // Set the scene background color
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	// main method
	public static void main(String[] args) {
		launch(args);

	}

	// method to make a good style for the buttons
	private Button createStyledButton(String text) {
		Button button = new Button(text);
		button.setStyle(
				"-fx-background-color: #009688; -fx-text-fill: white; -fx-font-family: 'Arial Black'; -fx-font-size: 18px;");

		// Hover effect to change button color to black
		button.setOnMouseEntered(e -> {
			button.setStyle(
					"-fx-background-color: black; -fx-text-fill: white; -fx-font-family: 'Arial Black'; -fx-font-size: 18px;");
		});
		button.setOnMouseExited(e -> {
			button.setStyle(
					"-fx-background-color: #009688; -fx-text-fill: white; -fx-font-family: 'Arial Black'; -fx-font-size: 18px;");

		});

		return button;
	}

	// Method to create the background with a color
	public Background getBackground1() {
		BackgroundFill backgroundFill = new BackgroundFill(Color.DARKSLATEBLUE, CornerRadii.EMPTY, Insets.EMPTY);
		return new Background(backgroundFill);
	}

	// method to check if file is valid
	private boolean areFileTagsBalanced(String fileContent) {
		// create a stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack stack = new LinkedStack();
		cursorStack.insertAtHead(stack, p);
		// Split the file content into lines
		String[] lines = fileContent.split("\n");

		for (String line : lines) {
			// Process each line
			for (char ch : line.toCharArray()) {

				if (ch == '<') {
					stack.push(ch);
				} else if (ch == '>') {
					// Check if there is an opening tag on the stack
					if (stack.isEmpty()) {
						return false; // Tags are not balanced
					} else {
						stack.pop();
					}
				}
			}
		}

		// Check if the stack is empty after processing all lines
		return stack.isEmpty();
	}

	// method to process the equation
	private void processEquations(String fileContent) {
		// create a stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack stack = new LinkedStack();
		cursorStack.insertAtHead(stack, p);
		try {
			BufferedReader br = new BufferedReader(new StringReader(fileContent));
			String line;
			int sectionCount = 0;
			// Iterate through lines until reaching the desired section
			while ((line = br.readLine()) != null && sectionCount <= currentSectionIndex) {
				if (line.contains("<section>")) {
					sectionCount++;
				}
			}
			//if statement to clear the text area
			if (line != null) {
				textArea.clear();

				//while loop to process the equations in a specific section
				while (line != null && !line.contains("</section>")) {
					//make the infix equations
					if (line.contains("<infix>")) {
						textArea.appendText("infix : \n");
						//loop for all the equations 
						while ((line = br.readLine()) != null && !line.contains("</infix>")) {
							if (line.contains("<equation>")) {
								String equationTag = "<equation>";
								String endTag = "</equation>";
								int startIndex = line.indexOf(equationTag) + equationTag.length();
								int endIndex = line.indexOf(endTag);
								//if statement to extract the equation
								if (startIndex >= 0 && endIndex >= 0) {
									String equation = line.substring(startIndex, endIndex);
									//check if the equation is a valid infix
									if (isValidInfix(equation)) {
										String postfix = infixToPostfix(equation);
										//try and catch to handle any exception
										try {
											//print the result in the text area
											textArea.appendText("* " + equation + " ==> " + postfix + " ==>"
													+ evaluatePostfix(postfix) + "\n");
										} catch (ArithmeticException e) {
											//print an invalid if there is something wrong
											textArea.appendText("* " + equation);
											textArea.appendText(" ==> Invalid equation  \n");
										}
									} else {
										//print an invalid if there is something wrong
										textArea.appendText("* " + equation + " ==> Invalid equation  \n");
									}
								} else {
									// Handle the case where the equation tag is not well-formed
									textArea.appendText("* " + "Invalid equation \n");
								}
							}
						}
					}
					//if statement to deal with the postfix equation
					if (line.contains("<postfix>")) {
						textArea.appendText("Postfix : \n");
						//while loop to extract the equations
						while ((line = br.readLine()) != null && !line.contains("</postfix>")) {
							if (line.contains("<equation>")) {
								String equationTag = "<equation>";
								String endTag = "</equation>";
								int startIndex = line.indexOf(equationTag) + equationTag.length();
								int endIndex = line.indexOf(endTag);
								//extract the equation
								if (startIndex >= 0 && endIndex >= 0) {
									String equation = line.substring(startIndex, endIndex);
									//check if the equation is a valid postfix
									if (isPostfixExpression(equation)) {
										String prefix = postfixToPrefix(equation);
										//try and catch to handle any exception
										try {
											//print the result in the text area
											textArea.appendText("* " + equation + " ==> " + prefix + " ==>"
													+ evaluatePrefix(prefix) + "\n");
										} catch (ArithmeticException e) {
											//print an invalid if there is something wrong
											textArea.appendText("* " + equation);
											textArea.appendText(" ==> Invalid equation  \n");
										}
									} else {
										//print an invalid if there is something wrong
										textArea.appendText("* " + equation + " ==> Invalid equation  \n");
									}
								} else {
									// Handle the case where the equation tag is not well-formed
									textArea.appendText("* " + "Invalid equation \n");
								}
							}
						}
					}
					//to fix the infix problem
					line = br.readLine();

				}
			} else {
				//for the next and prev buttons
				currentSectionIndex = sectionCount;
			}
		} catch (IOException e) {
			// Handle IOException
		}
	}
	//method to check if the file is valid
	private boolean areTagsValid(String fileContent) {
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack stack = new LinkedStack();
		cursorStack.insertAtHead(stack, p);

		// Use regular expression to find all XML tags
		Pattern pattern = Pattern.compile("<(/?\\w+)>");
		Matcher matcher = pattern.matcher(fileContent);

		while (matcher.find()) {
			String tag = matcher.group(1);

			if (tag.startsWith("/")) {
				// Closing tag
				if (stack.isEmpty() || !tag.substring(1).equals(String.valueOf(stack.pop()))) {
					return false; // Invalid closing tag
				}
			} else {
				// Opening tag
				stack.push(tag);
			}
		}

		// The stack should be empty if all tags are properly closed
		return stack.isEmpty();
	}
	//method to change from infix to postfix
	public String infixToPostfix(String infix) {
		//stringbuilder to store the result
		StringBuilder postfix = new StringBuilder();
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack operatorStack = new LinkedStack();
		cursorStack.insertAtHead(operatorStack, p);
		String[] tokens = infix.split(" ");
		//for loop to do the changing
		for (String token : tokens) {
			if (isNumber(token)) {
				postfix.append(token).append(" ");
			} else if (token.equals("(")) {//push the '('
				operatorStack.push('(');
			} else if (token.equals(")")) {//pop the '('
				while (!operatorStack.isEmpty() && (char) operatorStack.peek() != '(') {
					postfix.append(operatorStack.pop()).append(" ");//store in the stringbuilder
				}
				if (!operatorStack.isEmpty() && (char) operatorStack.peek() == '(') {
					operatorStack.pop(); // Pop the '('
				} else {//if the equation is invalid
					return "Invalid equation";
				}
			} else {
				char currentChar = token.charAt(0);
				//compare between the operators
				while (!operatorStack.isEmpty() && precedence((char) operatorStack.peek()) >= precedence(currentChar)) {
					postfix.append(operatorStack.pop()).append(" ");
				}
				operatorStack.push(currentChar);
			}
		}

		while (!operatorStack.isEmpty()) {//check if there is operators left
			if ((char) operatorStack.peek() == '(') {//check if its invalid
				return "Invalid equation";
			}
			postfix.append(operatorStack.pop()).append(" ");//add it to the stringbuilder
		}

		return postfix.toString();//return the result
	}

	public double evaluatePostfix(String postfixExpression) {
	    // Create a cursor stack to keep track of the position in the linked list
		int p = cursorStack.createList();
		LinkedStack operandStack = new LinkedStack();
		cursorStack.insertAtHead(operandStack, p);
	    // Split the postfix expression into tokens
		String[] tokens = postfixExpression.split(" ");
	    // Iterate through each token in the postfix expression
		for (String token : tokens) {
	        // Check if the token is a number
			if (isNumber(token)) {
				operandStack.push(Double.parseDouble(token));
			} else if (token.equals("^")) {
	            // Exponentiation case
				if (operandStack.size < 2) {
					throw new ArithmeticException("Not enough operands for operator " + token);
				}
				double exponent = (double) operandStack.pop();
				double base = (double) operandStack.pop();
				operandStack.push(Math.pow(base, exponent));
			} else {
	            // Operator case
				if (!operandStack.isEmpty()) {
					double operand2 = (double) operandStack.pop();
					if (!operandStack.isEmpty()) {
						double operand1 = (double) operandStack.pop();
	                    // Perform the operation based on the operator
						switch (token) {
						case "+":
							operandStack.push(operand1 + operand2);
							break;
						case "-":
							operandStack.push(operand1 - operand2);
							break;
						case "*":
							operandStack.push(operand1 * operand2);
							break;
						case "/":
                            // Check for division by zero
							if (operand2 == 0) {
								throw new ArithmeticException("Division by zero");
							}
							operandStack.push(operand1 / operand2);
							break;
						}
					} else {
	                    // Not enough operands for the operator
						throw new ArithmeticException("Not enough operands for operator " + token);
					}
				} else {
                    // Not enough operands for the operator
					throw new ArithmeticException("Not enough operands for operator " + token);

				}
			}
		}
	    // Check if there is a single result on the stack
		if (!operandStack.isEmpty()) {
			return (double) operandStack.pop();
		} else {
	        // No result on the stack, invalid postfix expression
			return 0; 
		}
	}
	//check if this string is a number
	private boolean isNumber(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	//Returns the precedence level of an operator
	private int precedence(char operator) {
		switch (operator) {
		case '+':
		case '-':
			return 1;
		case '*':
		case '/':
			return 2;
		case '^':
			return 3;
		default:
			return 0;
		}
	}

	public boolean isValidInfix(String infixExpression) {
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack parenthesesStack = new LinkedStack();
		cursorStack.insertAtHead(parenthesesStack, p);
		boolean lastWasDigit = false;
		boolean lastBeforeSpace = false;
		int operandCount = 0;
		int operatorCount = 0;
	    // Iterate through each character in the infix expression
		for (int i = 0; i < infixExpression.length(); i++) {
			char currentChar = infixExpression.charAt(i);
			if (Character.isDigit(currentChar)) {
	            // Check if the previous character was not a digit
				if (!lastWasDigit) {
					// Increment operand count only if the previous character was not a digit
					operandCount++;
					lastWasDigit = true;
				}
			} else if (isOperator(currentChar)) {
	            // Increment operator count
				operatorCount++;
				lastWasDigit = false;
			} else if (currentChar == '(') {
	            // Push '(' onto the stack
				parenthesesStack.push('(');
				lastWasDigit = false;
			} else if (currentChar == ')') {
	            // Check if the stack is empty
				if (parenthesesStack.isEmpty()) {
					return false;
				}
	            // Pop '(' from the stack
				parenthesesStack.pop();
				lastWasDigit = false;
			} 
			

		}
	    // return if the parentheses stack is empty and operand and operator counts are valid
		return parenthesesStack.isEmpty() && (operandCount > 0 && operatorCount == operandCount - 1);
	}
	//Checks if a character is an operator.
	private boolean isOperator(char ch) {
		return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^';
	}
	//Converts a postfix expression to prefix notation.
	public String postfixToPrefix(String postfixExpression) {
		StringBuilder prefix = new StringBuilder();
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack operatorStack = new LinkedStack();
		cursorStack.insertAtHead(operatorStack, p);
	    // Split the postfix expression into tokens
		String[] tokens = postfixExpression.split(" ");
	    // Iterate through each token in the postfix expression
		for (String token : tokens) {
			if (isNumber(token)) {
	            // Push numbers onto the stack
				operatorStack.push(token);
			} else {
	            // Pop two operands and push the result along with the operator
				String operand2 = (String) operatorStack.pop();
				String operand1 = (String) operatorStack.pop();
				operatorStack.push(token + " " + operand1 + " " + operand2);
			}
		}
	    // The result should be the final expression on the stack
		return (String) operatorStack.pop();
	}
	//Evaluates a prefix expression.
	public double evaluatePrefix(String prefixExpression) {
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack operandStack = new LinkedStack();
		cursorStack.insertAtHead(operandStack, p);
	    // Split the prefix expression into tokens
		String[] tokens = prefixExpression.split(" ");
	    // Iterate through each token in reverse order
		for (int i = tokens.length - 1; i >= 0; i--) {
			String token = tokens[i];
			if (isNumber(token)) {
	            // Push numbers onto the stack
				operandStack.push(Double.parseDouble(token));
			} else if (isOperator(token)) {
	            // Pop two operands and perform the operation
				if (operandStack.size < 2) {
					throw new ArithmeticException("Not enough operands for operator " + token);
				}
				double operand1 = (double) operandStack.pop();
				double operand2 = (double) operandStack.pop();
	            // Perform the operation based on the operator
				switch (token) {
				case "+":
					operandStack.push(operand1 + operand2);
					break;
				case "-":
					operandStack.push(operand1 - operand2);
					break;
				case "*":
					operandStack.push(operand1 * operand2);
					break;
				case "/":
                    // Check for division by zero
					if (operand2 == 0) {
						throw new ArithmeticException("Division by zero");
					}
					operandStack.push(operand1 / operand2);
					break;
				case "^":
					operandStack.push(Math.pow(operand1, operand2));
					break;
				}
			}
		}
	    // Check if there is a single result on the stack
		if (!operandStack.isEmpty()) {
			return (double) operandStack.pop();
		} else {
	        // No result on the stack, invalid prefix expression
			throw new ArithmeticException("Error: Invalid prefix expression");
		}
	}
	//Checks if a token is an operator.
	private boolean isOperator(String token) {
		return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
	}
	//Checks if a given expression is a postfix expression.
	public boolean isPostfixExpression(String expression) {
		//create a new stack in the cursor array
		int p = cursorStack.createList();
		LinkedStack operandStack = new LinkedStack();
		cursorStack.insertAtHead(operandStack, p);
	    // Split the expression into tokens
		String[] tokens = expression.split(" ");
	    // Iterate through each token in the expression
		for (String token : tokens) {
			if (isNumber(token)) {
	            // Push numbers onto the stack
				operandStack.push(token);
			} else if (isOperator(token)) {
	            // Check if there are enough operands for the operator
				if (operandStack.size < 2) {
					return false; // Not enough operands for the operator
				}
				operandStack.pop(); // Pop one operand
				operandStack.pop(); // Pop another operand
				operandStack.push("result"); // Push a placeholder for the result
			} else {
				return false; // Invalid token (neither operand nor operator)
			}
		}

		return operandStack.size == 1 && operandStack.peek().equals("result");
	}

}
