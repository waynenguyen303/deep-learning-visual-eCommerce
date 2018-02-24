import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import okhttp3.OkHttpClient;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.typography.hershey.HersheyFont;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Naga on 22-01-2017.
 */
public class ClarifaiExample {
    public static void main(String[] args) throws IOException {


        final ClarifaiClient client = new ClarifaiBuilder("KKQIegBW9uOl_3vaMSzqq4QCfPNyNBvB7XNBz1vE", "xsY48eiDhhsFo5M7HE3F71ZYkB_tEQmemlWekTgG")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync(); // or use .build() to get a Future<ClarifaiClient>
        client.getToken();


        ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                .withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of(new File("input/maxresdefault.jpg")))
                )
                .executeSync();
        List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
        if (predictions.isEmpty()) {
            System.out.println("No Predictions");
        }
        else{
            MBFImage image = ImageUtilities.readMBF(new File("input/maxresdefault.jpg"));
            int x = image.getWidth();
            int y = image.getHeight();


            List<Concept> data = predictions.get(0).data();
            for (int i = 0; i < data.size(); i++) {
                System.out.println(data.get(i).name() + " - " + data.get(i).value());
                image.drawText(data.get(i).name(), (int)Math.floor(Math.random()*x), (int) Math.floor(Math.random()*y), HersheyFont.ASTROLOGY, 20, RGBColour.RED);
            }
            DisplayUtilities.displayName(image, "videoFrames");

        }

    }
}
