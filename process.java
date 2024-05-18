import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/uploadImage")
public class ImageUploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Assuming you have a file input with name 'image'
        Part filePart = request.getPart("image");
        String fileName = filePart.getSubmittedFileName();
        
        function previewImage() {
    var oFReader = new FileReader();
    oFReader.readAsDataURL(document.getElementById("imageInput").files[0]);

    oFReader.onload = function (oFREvent) {
        // Set the uploaded image as source
        document.getElementById("uploadedImage").src = oFREvent.target.result;

        // Wait for OpenCV to load
        cv['onRuntimeInitialized']=()=>{
            // Convert the image to OpenCV format
            let imgElement = document.getElementById('uploadedImage');
            let mat = cv.imread(imgElement);
            let img_y_cr_cb = new cv.Mat();
             cv.cvtColor(image, img_y_cr_cb, cv.COLOR_BGR2YCrCb, 0);

let channels = new cv.MatVector();
cv.split(img_y_cr_cb, channels);

let y_eq = new cv.Mat();
cv.equalizeHist(channels.get(0), y_eq);

let img_y_cr_cb_eq = new cv.Mat();
let merged_channels = new cv.MatVector();
merged_channels.push_back(y_eq);
merged_channels.push_back(channels.get(1));
merged_channels.push_back(channels.get(2));
cv.merge(merged_channels, img_y_cr_cb_eq);

let img_rgb_eq = new cv.Mat();
cv.cvtColor(img_y_cr_cb_eq, img_rgb_eq, cv.COLOR_YCrCb2BGR, 0);

let gray = new cv.Mat();
cv.cvtColor(image, gray, cv.COLOR_BGR2GRAY, 0);

let thresh = new cv.Mat();
let ret = cv.threshold(gray, thresh, 0, 255, cv.THRESH_BINARY_INV + cv.THRESH_OTSU);

let hsv = new cv.Mat();
cv.cvtColor(image, hsv, cv.COLOR_BGR2HSV, 0);

let ycrcb = new cv.Mat();
cv.cvtColor(image, ycrcb, cv.COLOR_BGR2YCrCb, 0);

let lower_range_hsv = new cv.Mat(hsv.rows, hsv.cols, hsv.type(), [0, 58, 30, 0]);
let upper_range_hsv = new cv.Mat(hsv.rows, hsv.cols, hsv.type(), [33, 255, 255, 0]);
let mask_hsv = new cv.Mat();
cv.inRange(hsv, lower_range_hsv, upper_range_hsv, mask_hsv);

let lower_range_ycrcb = new cv.Mat(ycrcb.rows, ycrcb.cols, ycrcb.type(), [0, 133, 77, 0]);
let upper_range_ycrcb = new cv.Mat(ycrcb.rows, ycrcb.cols, ycrcb.type(), [255, 173, 127, 0]);
let mask_ycrcb = new cv.Mat();
cv.inRange(ycrcb, lower_range_ycrcb, upper_range_ycrcb, mask_ycrcb);

let mask = new cv.Mat();
cv.bitwise_and(mask_hsv, mask_ycrcb, mask);

let result = new cv.Mat();
cv.bitwise_and(image, image, result, mask);
var rgbMat = new cv.Mat();
result.convertTo(rgbMat, cv.CV_8UC3, 1 / 255);

let r = cv.mean(rgbMat)[2];
let g = cv.mean(rgbMat)[1];
let b = cv.mean(rgbMat)[0];
        };
    };
};


        // For now, let's just save the uploaded file
        String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();
        filePart.write(uploadPath + File.separator + fileName);

        // Set response content type
        response.setContentType("text/html");

        // Send response to confirm file upload
        PrintWriter out = response.getWriter();
        out.println("<h3>Image uploaded successfully!</h3>");
        out.println("<img src='uploads/" + fileName + "' alt='Uploaded Image'/>");
    }
}
