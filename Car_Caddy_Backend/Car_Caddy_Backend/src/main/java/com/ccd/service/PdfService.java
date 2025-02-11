package com.ccd.service;

import com.ccd.model.Car;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

	public byte[] generateCarDetailsPdf(List<Car> cars) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			Document document = new Document();
			PdfWriter.getInstance(document, outputStream);
			document.open();

			// Title
			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
			Paragraph title = new Paragraph("Car Details", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			title.setSpacingAfter(20);
			document.add(title);

			// Create a table with 5 columns
			Table table = new Table(5); // Number of columns
			table.setPadding(5); // Cell padding
			table.setSpacing(2); // Cell spacing
			table.setWidths(new int[] { 15, 25, 25, 15, 20 }); // Relative column widths
			table.setAlignment(Element.ALIGN_CENTER);

			// Add table headers
			table.addCell(new Phrase("Vehicle ID"));
			table.addCell(new Phrase("Vehicle Type"));
			table.addCell(new Phrase("Model"));
			table.addCell(new Phrase("Year"));
			table.addCell(new Phrase("Status"));

			// Add table rows
			for (Car car : cars) {
				table.addCell(String.valueOf(car.getCarId()));
				table.addCell(car.getVehicleType());
				table.addCell(car.getModel());
				table.addCell(String.valueOf(car.getYearOfManufacture()));
				table.addCell(car.getStatus());
			}

			document.add(table);
			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}
}
