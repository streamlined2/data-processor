package com.streamlined.dataprocessor.reporter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.streamlined.dataprocessor.processor.ProcessingResult;

public class Reporter {

	private static final int BUFFER_SIZE = 16 * 1024;

	private final XmlMapper mapper;

	public Reporter() {
		mapper = new XmlMapper();
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		SimpleModule module = new SimpleModule();
		module.addSerializer(ProcessingResult.class,new ProcessingResult.Serializer());
		mapper.registerModule(module);
	}

	public void save(Path resultFile, ProcessingResult processingResult) {
		try (var stream = Files.newOutputStream(resultFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
				var bufferedStream = new BufferedOutputStream(stream, BUFFER_SIZE)) {
			var writer = mapper.writer().withRootName("statistics");
			writer.writeValue(bufferedStream, processingResult);
		} catch (IOException e) {
			throw new ReportingException("Error occurred while saving report", e);
		}
	}

}
