package com.example.pneumIA.controllers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Base64;
import javax.xml.bind.DatatypeConverter;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.pneumIA.models.ChartModel;
import com.example.pneumIA.models.TableModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;



@RestController
public class reportController {
	@Autowired
    RestTemplate restTemplate;
	
	@PostMapping("/binaryPrediction")
	public JsonNode binaryPrediction(@RequestParam("xray") MultipartFile file) throws IOException {
        // load file from /src/test/resources
        byte[] fileContent = file.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
        = new LinkedMultiValueMap<>();
        body.add("xray", encodedString);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);

       String serverUrl = "http://10.10.10.27:8000/api/cxr_binary_predict_pathologie/";

       RestTemplate restTemplate = new RestTemplate();
       ResponseEntity<JsonNode> response = restTemplate
         .postForEntity(serverUrl, requestEntity, JsonNode.class);
        
        return response.getBody();

        
    }
	
	@PostMapping("/multiPrediction")
	public JsonNode multiPrediction(@RequestParam("xray") MultipartFile file) throws IOException {
        // load file from /src/test/resources
        byte[] fileContent = file.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
        = new LinkedMultiValueMap<>();
        body.add("xray", encodedString);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);

       String serverUrl = "http://10.10.10.27:8000/api/cxr_multi_predict_pathologie/";

       RestTemplate restTemplate = new RestTemplate();
       ResponseEntity<JsonNode> response = restTemplate
         .postForEntity(serverUrl, requestEntity, JsonNode.class);
        
        return response.getBody();

        
    }
	
	@PostMapping("/imageCheck")
	public JsonNode imageCheck(@RequestParam("xray") MultipartFile file) throws IOException {
        // load file from /src/test/resources
        byte[] fileContent = file.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body
        = new LinkedMultiValueMap<>();
        body.add("xray", encodedString);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);

       
        //URLEncoder uri = URLEncoder.encode(url);
        String serverUrl = ("http://10.10.10.27:8000/api/cxr_quality_check/");
       RestTemplate restTemplate = new RestTemplate();
       JsonNode response = restTemplate
         .postForObject(serverUrl, requestEntity, JsonNode.class);
        
        return response;

        
    }
	
	@PostMapping("/heatMap")
	public JsonNode heatMap(@RequestParam("xray") MultipartFile file) throws IOException {
        // load file from /src/test/resources
        byte[] fileContent = file.getBytes();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("xray", encodedString);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);

       String serverUrl = "http://10.10.10.27:8000/api/generate_pathologie_heatmap/";

       RestTemplate restTemplate = new RestTemplate();
       JsonNode response = restTemplate
         .postForObject(serverUrl, requestEntity, JsonNode.class);
        
        return response;   
    }
	
	
	
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping("/cxrReport")
	public ResponseEntity<byte[]> exportReport(@RequestParam("xray") MultipartFile xray) throws JRException, IOException {
        //load file and compile it
		
		boolean quality = imageCheck(xray).get("Quality").asBoolean();
		double anormalePredict = Math.floor(binaryPrediction(xray).get("data").get("Abonrmality prediction").get("Anormal").asDouble()*10000)/100;
		double normalePredict = Math.floor(binaryPrediction(xray).get("data").get("Abonrmality prediction").get("Normale").asDouble()*10000)/100;
		double Cardiomégalie = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Cardiomegalie").asDouble() * 10000)/100 ;
		double Emphysème = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Emphyseme").asDouble() * 10000)/100;
		double Epanchement = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Epanchement").asDouble() * 10000)/100;
		double Hernie = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Hernie").asDouble() * 10000)/100;
		double Infiltration = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Infiltration").asDouble() * 10000)/100;
		double Masse = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Masse").asDouble() * 10000)/100;
		double Nodule = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Nodule").asDouble() * 10000)/100;
		double Atélectasie = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Atelectasie").asDouble() * 10000)/100;
		double Pneumothorax = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Pneumothorax").asDouble() * 10000)/100;
		double Pleural_Thickening = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Pleural_Thickening").asDouble() * 10000)/100;
		double Pneumonie = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Pneumonie").asDouble() * 10000)/100;
		double Fibrose = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Fibrose").asDouble() * 10000)/100;
		double Œdème = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Oedeme").asDouble() * 10000)/100;
		double Consolidation = Math.floor(multiPrediction(xray).get("data").get("Multi pthologies predction").get("Consolidation").asDouble() * 10000)/100;
		JsonNode multiP = multiPrediction(xray).get("data").get("Multi pthologies predction");
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Double> chart1 = mapper.convertValue(multiP, 
				new TypeReference<HashMap<String, Double>>(){});
		HashMap<String, Double> chart2 = mapper.convertValue(multiP, 
				new TypeReference<HashMap<String, Double>>(){}); 
		HashMap<String, Double> chart3 = mapper.convertValue(multiP, 
				new TypeReference<HashMap<String, Double>>(){}); 
		List<String> pathologies = chart1.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
		//byte[] visionIA = heatMap(xray).getBody().getBytes(StandardCharsets.UTF_8);
		//byte[] imageIA = heatMap(xray).getBody();
		//List<String> pathologie = new ArrayList<String>(chart.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
		//List<Double> v = new ArrayList<Double>(chart.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()));
		List<TableModel> listTm = new ArrayList<TableModel>();
		listTm.add(new TableModel("Cardiomégalie",(Cardiomégalie>60)?"Positif":"Négatif",Cardiomégalie));
		listTm.add(new TableModel("Emphysème",(Emphysème>60)?"Positif":"Négatif",Emphysème));
		listTm.add(new TableModel("Epanchement",(Epanchement>60)?"Positif":"Négatif",Epanchement));
		listTm.add(new TableModel("Hernie",(Hernie>60)?"Positif":"Négatif",Hernie));
		listTm.add(new TableModel("Infiltration",(Infiltration>60)?"Positif":"Négatif",Infiltration));
		listTm.add(new TableModel("Masse",(Masse>60)?"Positif":"Négatif",Masse));
		listTm.add(new TableModel("Nodule",(Nodule>60)?"Positif":"Négatif",Nodule));
		listTm.add(new TableModel("Atélectasie",(Atélectasie>60)?"Positif":"Négatif",Atélectasie));
		listTm.add(new TableModel("Pneumothorax",(Pneumothorax>60)?"Positif":"Négatif",Pneumothorax));
		listTm.add(new TableModel("Pleural_Thickening",(Pleural_Thickening>60)?"Positif":"Négatif",Pleural_Thickening));
		listTm.add(new TableModel("Pneumonie",(Pneumonie>60)?"Positif":"Négatif",Pneumonie));
		listTm.add(new TableModel("Fibrose",(Fibrose>60)?"Positif":"Négatif",Fibrose));
		listTm.add(new TableModel("Œdème",(Œdème>60)?"Positif":"Négatif",Œdème));
		listTm.add(new TableModel("Consolidation",(Consolidation>60)?"Positif":"Négatif",Consolidation));
		List<ChartModel> listCm = new ArrayList<ChartModel>();
		listCm.add(new ChartModel("Cardiomégalie", "Pathologies", Cardiomégalie));
		listCm.add(new ChartModel("Emphysème", "Pathologies", Emphysème));
		listCm.add(new ChartModel("Epanchement", "Pathologies", Epanchement));
		listCm.add(new ChartModel("Hernie", "Pathologies", Hernie));
		listCm.add(new ChartModel("Infiltration", "Pathologies", Infiltration));
		listCm.add(new ChartModel("Masse", "Pathologies", Masse));
		listCm.add(new ChartModel("Nodule", "Pathologies", Nodule));
		listCm.add(new ChartModel("Atélectasie", "Pathologies", Atélectasie));
		listCm.add(new ChartModel("Pneumothorax", "Pathologies", Pneumothorax));
		listCm.add(new ChartModel("Pleural_Thickening", "Pathologies", Pleural_Thickening));
		listCm.add(new ChartModel("Pneumonie", "Pathologies", Pneumonie));
		listCm.add(new ChartModel("Fibrose", "Pathologies", Fibrose));
		listCm.add(new ChartModel("Œdème", "Pathologies", Œdème));
		listCm.add(new ChartModel("Consolidation", "Pathologies", Consolidation));
		List<ChartModel> listCm1 = new ArrayList<>();
		List<ChartModel> listCm2 = new ArrayList<>();
		List<ChartModel> listCm3 = new ArrayList<>();
		List<ChartModel> listCm4 = new ArrayList<>();
		Double maxValueInMap1 = Collections.max(chart1.values());
		String key = Collections.max(chart2.entrySet(), Map.Entry.comparingByValue()).getKey();
		chart2.entrySet().removeIf(entry -> (key == entry.getKey()));
		Double maxValueInMap2 = Collections.max(chart2.values());
		String keyy = Collections.max(chart3.entrySet(), Map.Entry.comparingByValue()).getKey();
		chart3.entrySet().removeIf(entry -> (keyy == entry.getKey()));
		String keyyy = Collections.max(chart3.entrySet(), Map.Entry.comparingByValue()).getKey();
		chart3.entrySet().removeIf(entry -> (keyyy == entry.getKey()));
		Double maxValueInMap3 = Collections.max(chart3.values());
		String image1 = heatMap(xray).get("data").get("heatmap_path_1").toString();
		String imageIA1 = image1.split(",")[1];
		byte[] decodedImage1 = DatatypeConverter.parseBase64Binary(imageIA1);
		String image2 = heatMap(xray).get("data").get("heatmap_path_2").toString();
		String imageIA2 = image2.split(",")[1];
		byte[] decodedImage2 = DatatypeConverter.parseBase64Binary(imageIA2);
		String image3 = heatMap(xray).get("data").get("heatmap_path_3").toString();
		String imageIA3 = image3.split(",")[1];
		byte[] decodedImage3 = DatatypeConverter.parseBase64Binary(imageIA3);
		
		 //Map.Entry<String, Double> max1 = null;
		 //Map.Entry<String, Double> max2 = null;
		for(Entry<String,Double> entry : chart1.entrySet()) {
			if(entry.getValue()== maxValueInMap1) {
				listCm1.add(new ChartModel("Positif",entry.getKey(),entry.getValue()));
				listCm1.add(new ChartModel("Négatif",entry.getKey(),1-entry.getValue()));
			}			
		}
		for(Entry<String,Double> entry : chart2.entrySet()) {
			if(entry.getValue()== maxValueInMap2) {
				listCm2.add(new ChartModel("Positif",entry.getKey(),entry.getValue()));
				listCm2.add(new ChartModel("Négatif",entry.getKey(),1-entry.getValue()));
			}			
		}
		for(Entry<String,Double> entry : chart3.entrySet()) {
			if(entry.getValue()== maxValueInMap3) {
				listCm3.add(new ChartModel("Positif",entry.getKey(),entry.getValue()));
				listCm3.add(new ChartModel("Négatif",entry.getKey(),1-entry.getValue()));
			}			
		}
		
		listCm4.add(new ChartModel("Anoramle","Prédiction",anormalePredict));
		listCm4.add(new ChartModel("Normale","Prédiction",normalePredict));
		
		
		// dynamic parameters required for report
		Map<String, Object> Params = new HashMap<String, Object>();
		Params.put("Abnormality", anormalePredict);
		Params.put("Scan", ImageIO.read(new ByteArrayInputStream(JRLoader.loadBytes(xray.getResource().getInputStream()))));
		Params.put("imageIA1", ImageIO.read(new ByteArrayInputStream(decodedImage1)));
		Params.put("imageIA2", ImageIO.read(new ByteArrayInputStream(decodedImage2)));
		Params.put("imageIA3", ImageIO.read(new ByteArrayInputStream(decodedImage3)));
		Params.put("pathologies",pathologies);
		Params.put("CollectionBeanParam",new JRBeanCollectionDataSource(listTm));
		Params.put("chartBeanParam",new JRBeanCollectionDataSource(listCm));
		Params.put("chartBeanParam1", new JRBeanCollectionDataSource(listCm1));
		Params.put("chartBeanParam2", new JRBeanCollectionDataSource(listCm2));
		Params.put("chartBeanParam3", new JRBeanCollectionDataSource(listCm3));
		Params.put("chartBeanParam4", new JRBeanCollectionDataSource(listCm4));
		
		//Params.put("pathologies", keys);
		
		//Params.put("xray", xray);
		if(quality) {
		
		if(normalePredict < 0.4) {
			JasperPrint Report = JasperFillManager.fillReport(JasperCompileManager.compileReport(ResourceUtils
					.getFile("classpath:rapportMulti.jrxml").getAbsolutePath()),
					Params, new JREmptyDataSource());

			HttpHeaders headers = new HttpHeaders();
			
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "cxrReport.pdf");
			
			return new ResponseEntity<byte[]>(JasperExportManager.exportReportToPdf(Report), headers, HttpStatus.OK);
		}
		else {
			JasperPrint Report = JasperFillManager.fillReport(JasperCompileManager.compileReport(ResourceUtils
					.getFile("classpath:rapportNormal.jrxml").getAbsolutePath()),
					Params, new JREmptyDataSource());

			HttpHeaders headers = new HttpHeaders();
			
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "cxrReport.pdf");
			
			return new ResponseEntity<byte[]>(JasperExportManager.exportReportToPdf(Report), headers, HttpStatus.OK);
		}
		}
		else
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@GetMapping("/test")
	public byte[] test(@RequestParam("xray") MultipartFile xray) throws IOException {
		String imageIA = heatMap(xray).get("data").get("heatmap_path_2").toString();
		String image = imageIA.split(",")[1];
		byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image);
		return decodedBytes;
		
	}

}

	/*@GetMapping("/employee/records/report")
	public ResponseEntity<byte[]> getEmployeeRecordReport() {

		try {
			// create employee data
			Employee emp1 = new Employee(1, "AAA", "BBB", "A city");
			Employee emp2 = new Employee(2, "XXX", "ZZZ", "B city");

			List<Employee> empLst = new ArrayList<Employee>();
			empLst.add(emp1);
			empLst.add(emp2);

			//dynamic parameters required for report
			Map<String, Object> empParams = new HashMap<String, Object>();
			empParams.put("CompanyName", "TechGeekNext");
			empParams.put("employeeData", new JRBeanCollectionDataSource(empLst));

			JasperPrint empReport =
					JasperFillManager.fillReport
				   (
							JasperCompileManager.compileReport(
							ResourceUtils.getFile("classpath:employees-details.jrxml")
									.getAbsolutePath()) // path of the jasper report
							, empParams // dynamic parameters
							, new JREmptyDataSource()
					);
			
			HttpHeaders headers = new HttpHeaders();
			//set the PDF format
			headers.setContentType(MediaType.APPLICATION_PDF);
			headers.setContentDispositionFormData("filename", "employees-details.pdf");
			//create the report in PDF format
			return new ResponseEntity<byte[]>
					(JasperExportManager.exportReportToPdf(empReport), headers, HttpStatus.OK);
			
		} catch(Exception e) {
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}*/



