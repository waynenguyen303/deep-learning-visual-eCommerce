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
import java.io.*;


/**
 * Created by Naga on 24-01-2017.
 */
public class ImageAnnotation {
    public static void main(String[] args) throws IOException {
        final ClarifaiClient client = new ClarifaiBuilder("qHUs9cdjSiXwiIhoQ02ZwO3QFZet2uNPrxEGjinH", "vzsuX7ldq5nCyv8kyc9Oh0dq56ugUuczaE-ITuao")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync(); // or use .build() to get a Future<ClarifaiClient>
        client.getToken();

        // my API KEY: fdf29a3cfd224795ac68e9a5fd2e871f
        // my client id: qHUs9cdjSiXwiIhoQ02ZwO3QFZet2uNPrxEGjinH
        // my client secret: vzsuX7ldq5nCyv8kyc9Oh0dq56ugUuczaE-ITuao
        // old appID: KKQIegBW9uOl_3vaMSzqq4QCfPNyNBvB7XNBz1vE
        // old appSecret: xsY48eiDhhsFo5M7HE3F71ZYkB_tEQmemlWekTgG

        File file = new File("output/mainframes-man");
        File[] files = file.listFiles();

        PrintStream out = new PrintStream(new FileOutputStream("projectdata-annotationOutput.txt"));

        for (int i=0; i<files.length;i++){
            ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                    .withInputs(
                            ClarifaiInput.forImage(ClarifaiImage.of(files[i]))
                    )
                    .executeSync();
            List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
            MBFImage image = ImageUtilities.readMBF(files[i]);
            int x = image.getWidth();
            int y = image.getHeight();

            String text = "*************" + files[i] + "***********";
            System.out.println("*************" + files[i] + "***********");

            out.print(text+"\n");

            List<Concept> data = predictions.get(0).data();
            for (int j = 0; j < data.size(); j++) {

                String text1 = data.get(j).name() + " - " + data.get(j).value();
                System.out.println(data.get(j).name() + " - " + data.get(j).value());

                out.print(text1+"\n");

                image.drawText(data.get(j).name(), (int)Math.floor(Math.random()*x), (int) Math.floor(Math.random()*y), HersheyFont.ASTROLOGY, 20, RGBColour.RED);
            }
            DisplayUtilities.displayName(image, "image" + i);
        }

        out.close();

    }
}
