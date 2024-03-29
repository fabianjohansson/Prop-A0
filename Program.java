

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class Program {
	public static void main(String[] args) {
		String inputFileName = null;
		String outputFileName = null;
		IParser parser = null;
		INode root = null; // Root of the parse tree.
		StringBuilder builder = null;
		FileOutputStream stream = null;
		OutputStreamWriter writer = null;
		
		try {
			try {
				if (args.length < 0)
					throw new Exception("Incorrect number of parameters to program.");

				inputFileName = "C:\\Users\\Michel\\IdeaProjects\\Prop-A0\\program2.txt";
				outputFileName = "C:\\Users\\Michel\\IdeaProjects\\Prop-A0\\out.txt";

				//inputFileName = args[0];
				//outputFileName = args[1];

				parser = new Parser();
				parser.open(inputFileName);
				root = parser.parse();
				builder = new StringBuilder();
				builder.append("PARSE TREE:\n");
				root.buildString(builder, 0);
				System.out.println(builder.toString());
				builder.append("\nEVALUATION:\n");
				builder.append(root.evaluate(null));
				System.out.println(builder.toString());
				
				stream = new FileOutputStream(outputFileName);
				writer = new OutputStreamWriter(stream);
				writer.write(builder.toString());
			}
			catch (Exception exception) {
				System.out.println("EXCEPTION : " + exception);
			}
			finally {
				if (parser != null)
					parser.close();
				if (writer != null)
					writer.close();
				if (stream != null)
					stream.close();
			}
		}
		catch (Exception exception) {
			System.out.println("EXCEPTION: " + exception);
		}
	}
}
