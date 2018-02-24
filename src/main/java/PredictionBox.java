import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PredictionBox {
    String symbol;
    Label predictionSymbol = new Label();
    HBox predictions = new HBox();
    Label[] predictionLabels;

    PredictionBox(String stock, double[] days, boolean[] predictionValues){
        symbol = stock;
        predictions.setMinSize(610,20);
        predictions.setPrefSize(610,20);
        predictions.setMaxSize(610,20);

        predictionSymbol.setFont(Font.font(null, FontWeight.BOLD, 14));
        predictionSymbol.setMinSize(80,20);

        predictionSymbol.setText(symbol);
        predictionLabels = new Label[days.length];

        for(int i = 0; i < days.length; i++){
            predictionLabels[i] = new Label();
            predictionLabels[i].setFont(Font.font(null, 14));
            predictionLabels[i].setMinSize(120,20);

            if(predictionValues[i]) {
                predictionLabels[i].setTextFill(Color.GREEN);
                predictionLabels[i].setText("RISE/MAINTAIN");
            }
            else{
                predictionLabels[i].setTextFill(Color.RED);
                predictionLabels[i].setText("FALL");
            }
        }

        predictions.getChildren().add(predictionSymbol);
        predictions.getChildren().addAll(predictionLabels);
    }

    public Node getNode() {
        return predictions;
    }
}