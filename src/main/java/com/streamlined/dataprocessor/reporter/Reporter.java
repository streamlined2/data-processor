package com.streamlined.dataprocessor.reporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.streamlined.dataprocessor.processor.ProcessingResult;

/**
 * Class Reporter composes report and saves it as XML file
 */
public class Reporter {

	private static final Logger log = Logger.getLogger(Reporter.class.getName());

	private static final String ROOT_ELEMENT = "statistics";
	private final XmlMapper mapper;

	public Reporter() {
		mapper = new XmlMapper();
		mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		SimpleModule module = new SimpleModule();
		module.addSerializer(ProcessingResult.class, new ProcessingResult.Serializer());
		mapper.registerModule(module);
	}

	/**
	 * Method saves result of processing {@code processingResult} as XML file at specified location {@code resultFile}
	 * @param resultFile
	 * @param processingResult
	 * @throws ReportingException if IOException occurs during output file serialization
	 */
	public void save(Path resultFile, ProcessingResult processingResult) {
		try (var bufferedWriter = Files.newBufferedWriter(resultFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			mapper.writer().withRootName(ROOT_ELEMENT).writeValue(bufferedWriter, processingResult);
		} catch (IOException e) {
			log.severe("Error occurred while saving report");
			throw new ReportingException("Error occurred while saving report", e);
		}
	}

}
