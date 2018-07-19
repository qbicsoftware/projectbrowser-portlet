/*******************************************************************************
 * QBiC Project qNavigator enables users to manage their projects. Copyright (C) "2016”
 * Christopher Mohr, David Wojnar, Andreas Friedrich
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package life.qbic.projectbrowser.helpers;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

public class UglyToPrettyNameMapper {
  // private Map<String,String> namesMapping = new HashMap<String, String>();
  private BidiMap<String, String> namesMapping = new DualHashBidiMap<String, String>();

  public UglyToPrettyNameMapper() {

    // openBIS experiment types translated
    namesMapping.put("Q_NGS_MEASUREMENT", "Next Generation Sequencing Run");
    namesMapping.put("Q_EXPERIMENTAL_DESIGN", "Sampling Units");
    namesMapping.put("Q_SAMPLE_EXTRACTION", "Sample Extraction");
    namesMapping.put("Q_SAMPLE_PREPARATION", "Sample Preparation");
    namesMapping.put("Q_PROJECT_DETAILS", "Project Details");
    namesMapping.put("UNKNOWN", "Experiment Unknown");
    namesMapping.put("Q_EXT_MS_QUALITYCONTROL", "MS Quality Control");
    namesMapping.put("Q_EXT_NGS_QUALITYCONTROL", "NGS Quality Control");
    namesMapping.put("Q_MICROARRAY_MEASUREMENT", "Microarray Measurement");
    namesMapping.put("Q_MS_MEASUREMENT", "MS Measurement");
    namesMapping.put("Q_NGS_EPITOPE_PREDICTION", "Prediction of MHC binding Epitopes");
    namesMapping.put("Q_NGS_FLOWCELL_RUN", "Flowcell Run");
    namesMapping.put("Q_NGS_HLATYPING", "HLA Typing");
    namesMapping.put("Q_NGS_IMMUNE_MONITORING", "Immune Monitoring");
    namesMapping.put("Q_NGS_MAPPING", "Mapping of NGS Reads");
    namesMapping.put("Q_NGS_SINGLE_SAMPLE_RUN", "Next-Generation Sequencing Run");
    namesMapping.put("Q_NGS_VARIANT_CALLING", "Variant Calling");
    namesMapping.put("Q_WF_MA_QUALITYCONTROL", "Microarray Quality Control Workflow");
    namesMapping.put("Q_WF_MS_MAXQUANT", "MaxQuant Workflow");
    namesMapping.put("Q_WF_MS_PEPTIDEID", "Peptide Identification Workflow");
    namesMapping.put("Q_WF_MS_QUALITYCONTROL", "MS Quality Control Worfklow");
    namesMapping.put("Q_WF_NGS_EPITOPE_PREDICTION", "Prediction of MHC binding Epitopes Workflow");
    namesMapping.put("Q_WF_NGS_HLATYPING", "HLA Typing Workflow");
    namesMapping.put("Q_WF_NGS_MERGE", "Merging of NGS Reads");
    namesMapping.put("Q_WF_NGS_QUALITYCONTROL", "NGS Quality Control Workflow");
    namesMapping.put("Q_WF_NGS_RNA_EXPRESSION_ANALYSIS", "RNA Expression Analysis Workflow");
    namesMapping.put("Q_WF_NGS_VARIANT_ANNOTATION", "Variant Annotation Workflow");
    namesMapping.put("Q_WF_NGS_VARIANT_CALLING", "Variant Calling Workflow");
    namesMapping.put("Q_WF_NGS_MAPPING", "NGS Read Alignment Workflow");
    namesMapping.put("Q_WF_MS_INDIVIDUALIZED_PROTEOME", "Individualized Proteins Workflow");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_ID", "Ligandomics Identification Workflow");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_QC", "Ligandomics Quality Control Workflow");
    namesMapping.put("Q_MHC_LIGAND_EXTRACTION", "MHC Ligand Extraction");
    namesMapping.put("Q_WF_NGS_SHRNA_COUNTING", "shRNA Counting Workflow");
    namesMapping.put("Q_WF_NGS_16S_TAXONOMIC_PROFILING", "16S Taxonomic Profiling Workflow");
    namesMapping.put("Q_HT_QPCR", "High-Throughput Quantitative Real-Time PCR");
    namesMapping.put("Q_BMI_GENERIC_IMAGING", "Imaging");
    namesMapping.put("Q_NGS_READ_MATCH_ALIGNMENT", "Read-Match Alignment");


    // deprecated experiment types
    namesMapping.put("INFO_EXPERIMENT", "Info (deprecated)");
    namesMapping.put("METABOLOMICS", "Metabolomics (deprecated)");
    namesMapping.put("MS_INJECT", "MS Inject (deprecated)");
    namesMapping.put("MS_QUANT", "MS Quantification (deprecated)");
    namesMapping.put("MS_SEARCH", "MS Search (deprecated)");
    namesMapping.put("NGS_ANNOTATION", "Variant Annotation (deprecated)");
    namesMapping.put("NGS_MAPPING", "NGS Read Mapping (deprecated)");
    namesMapping.put("NGS_QUANTIFICATION", "NGS Quantification (deprecated)");
    namesMapping.put("NGS_SEQUENCING", "NGS Measurement (deprecated)");
    namesMapping.put("NGS_SUBSTRACTION", "Substraction (deprecated)");
    namesMapping.put("NGS_VARIANT_CALLING", "Variant Calling (deprecated)");
    namesMapping.put("NMR", "NMR (deprecated)");
    namesMapping.put("RNA_MICROARRAY", "Microarray Measurement (deprecated)");
    namesMapping.put("SAMPLE_EXTRACTION", "Sample Extraction (deprecated)");
    namesMapping.put("IMMUNE_MONITORING", "Immune Monitoring (deprecated)");
    namesMapping.put("HLA_TYPING", "HLA Typing (deprecated)");
    namesMapping.put("EPITOPE_PREDICTION", "Eptiope Prediction (deprecated)");
    namesMapping.put("CLINICAL_SAMPLE", "Clinical Sample (deprecated)");
    namesMapping.put("BIOLOGICAL_EXPERIMENT", "Biological Experiment (deprecated)");
    namesMapping.put("PATIENT_INFORMATION", "Patient Information (deprecated)");

    // openBIS sample types translated
    namesMapping.put("Q_BIOLOGICAL_ENTITY", "Experimental Unit");
    namesMapping.put("Q_BIOLOGICAL_SAMPLE", "Extracted Sample");
    namesMapping.put("Q_TEST_SAMPLE", "Prepared Sample");
    namesMapping.put("Q_ATTACHMENT_SAMPLE", "Project Attachment");
    namesMapping.put("Q_EXT_MS_QUALITYCONTROL_RUN", "External MS Quality Control Run");
    namesMapping.put("Q_EXT_NGS_QUALITYCONTROL_RUN", "External NGS Quality Control Run");
    namesMapping.put("Q_MICROARRAY_RUN", "Microarray Run");
    namesMapping.put("Q_MS_RUN", "Mass Spectrometry Run");
    namesMapping.put("Q_NGS_EPITOPES", "MHC Binding Epitopes");
    namesMapping.put("Q_NGS_FLOWCELL_RUN", "Flowcell Run");
    namesMapping.put("Q_NGS_HLATYPING", "HLA Typing Run");
    namesMapping.put("Q_NGS_IMMUNE_MONITORING", "Immune Monitoring");
    namesMapping.put("Q_NGS_MAPPING", "Mapping of NGS Reads");
    namesMapping.put("Q_NGS_SINGLE_SAMPLE_RUN", "Next-Generation Sequencing Run");
    namesMapping.put("Q_NGS_VARIANT_CALLING", "Variant Calling Run");
    namesMapping.put("Q_WF_MA_QUALITYCONTROL_RUN", "Microarray Quality Control Workflow Run");
    namesMapping.put("Q_WF_MS_MAXQUANT_RUN", "MaxQuant Workflow Run");
    namesMapping.put("Q_WF_MS_PEPTIDEID_RUN", "Peptide Identification Workflow Run");
    namesMapping.put("Q_WF_MS_QUALITYCONTROL_RUN", "MS Quality Control Workflow Run");
    namesMapping.put("Q_WF_NGS_EPITOPE_PREDICTION_RUN", "Epitope Prediction Workflow Run");
    namesMapping.put("Q_WF_NGS_HLATYPING_RUN", "HLA Typing Workflow Run");
    namesMapping.put("Q_WF_NGS_QUALITYCONTROL_RUN", "NGS Quality Control Workflow Run");
    namesMapping.put("Q_WF_NGS_RNA_EXPRESSION_ANALYSIS_RUN", "RNA Expression Workflow Run");
    namesMapping.put("Q_WF_NGS_VARIANT_ANNOTATION_RUN", "Variant Annotation Workflow Run");
    namesMapping.put("Q_WF_NGS_VARIANT_CALLING_RUN", "Variant Calling Workflow Run");
    namesMapping.put("Q_WF_NGS_MAPPING_RUN", "NGS Read Alignment Workflow Run");
    namesMapping.put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_RUN", "Individualized Proteins Workflow Run");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_ID_RUN", "Ligandomics Identification Workflow Run");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_QC_RUN", "Ligandomics Quality Control Workflow Run");
    namesMapping.put("Q_MHC_LIGAND_EXTRACT", "MHC Ligand Extract");
    namesMapping.put("Q_WF_NGS_SHRNA_COUNTING_RUN", "shRNA Counting Workflow Run");
    namesMapping
        .put("Q_WF_NGS_16S_TAXONOMIC_PROFILING_RUN", "16S Taxonomic Profiling Workflow Run");
    namesMapping.put("Q_HT_QPCR_RUN", "High-Throughput qPCR Run");
    namesMapping.put("Q_BMI_GENERIC_IMAGING_RUN", "Imaging Run");
    namesMapping.put("Q_NGS_READ_MATCH_ALIGNMENT_RUN", "Read-Match Alignment Run");


    // deprecated sample types
    namesMapping.put("BIOLOGICAL", "Biological Sample (deprecated)");
    namesMapping.put("BLOOD_SAMPLE", "Blood Sample (deprecated)");
    namesMapping.put("DNA", "DNA Sample (deprecated)");
    namesMapping.put("EPITOPE_PREDICTION", "Epitope Prediction Sample (deprecated)");
    namesMapping.put("HCC_SAMPLE", "HCC Sample (deprecated)");
    namesMapping.put("HLA_TYPING", "HLA Typing Sample (deprecated)");
    namesMapping.put("IMMUNE_MONITORING", "Immune Monitoring Sample (deprecated)");
    namesMapping.put("IMMUNO_MONITORING", "Immuno Monitoring Sample (deprecated)");
    namesMapping.put("LIGANDOMICS", "Ligandomics Sample (deprecated)");
    namesMapping.put("MS_INJECTION", "MS Injection Sample (deprecated)");
    namesMapping.put("NGS", "NGS Sample (deprecated)");
    namesMapping.put("MS_QUANT", "MS Quantification Sample (deprecated)");
    namesMapping.put("NGS_ANNO", "Variant Annotation Sample (deprecated)");
    namesMapping.put("NGS_MAP", "NGS Mapping Sample (deprecated)");
    namesMapping.put("NGS_QUANT", "NGS Quantification Sample (deprecated)");
    namesMapping.put("NGS_SEQ", "NGS Sequencing Sample (deprecated)");
    namesMapping.put("NGS_SEQUENCING", "NGS Measurement Sample (deprecated)");
    namesMapping.put("NGS_SUBSTRACT", "NGS Substraction Sample (deprecated)");
    namesMapping.put("NGS_VARIANT_CALLING", "Variant Calling Sample (deprecated)");
    namesMapping.put("NMR_INJECTION", "NMR Injection Sample (deprecated)");
    namesMapping.put("NORMAL_SAMPLE", "Normal TIssue Sample (deprecated)");
    namesMapping.put("PLASMA", "Plasma Sample (deprecated)");
    namesMapping.put("PROJECT_INFORMATION", "Project Information (deprecated)");
    namesMapping.put("PROTEIN", "Protein Sample (deprecated)");
    namesMapping.put("RNA", "RNA Sample (deprecated)");
    namesMapping.put("SEARCH", "Search Sample (deprecated)");
    namesMapping.put("TUMOR_SAMPLE", "Tumor Sample (deprecated)");
    namesMapping.put("NGS_QUANT", "NGS Quantification Sample (deprecated)");

    // openBIS dataset types translated
    namesMapping.put("Q_NGS_HLATYPING_DATA", "HLA Typing Results");
    namesMapping.put("UNKNOWN", "Unknown Sample");
    namesMapping.put("Q_EXT_MS_QUALITYCONTROL_RESULTS",
        "Mass Spectrometry Quality Control (External)");
    namesMapping.put("Q_EXT_NGS_QUALITYCONTROL_RESULTS", "NGS Quality Control (External)");
    namesMapping.put("Q_MA_RAW_DATA", "Microarray Raw Data");
    namesMapping.put("Q_MS_RAW_DATA", "Mass Spectrometry Raw Data");
    namesMapping.put("Q_NGS_IMMUNE_MONITORING_DATA", "Immune Monitoring Data");
    namesMapping.put("Q_NGS_MAPPING_DATA", "Mapped NGS Reads");
    namesMapping.put("Q_NGS_RAW_DATA", "NGS Raw Data");
    namesMapping.put("Q_NGS_VARIANT_CALLING_DATA", "Variant Calling Data");
    namesMapping.put("Q_PROJECT_DATA", "Project related Data");
    namesMapping.put("Q_WF_MA_QUALITYCONTROL_LOGS", "Microarray Quality Control Logs");
    namesMapping.put("Q_WF_MA_QUALITYCONTROL_RESULTS", "Microarray Quality Control");
    namesMapping.put("Q_WF_MS_MAXQUANT_ORIGINAL_OUT", "MaxQuant Outfile");
    namesMapping.put("Q_WF_MS_MAXQUANT_RESULTS", "MaxQuant Results");
    namesMapping.put("Q_WF_MS_MAXQUANT_LOGS", "MaxQuant Logs");
    namesMapping.put("Q_WF_MS_PEPTIDEID_LOGS", "Peptide Identification Logs");
    namesMapping.put("Q_WF_MS_PEPTIDEID_RESULTS", "Peptide Identification Results");
    namesMapping.put("Q_WF_MS_QUALITYCONTROL_LOGS", "Mass Spectrometry Quality Control Logs");
    namesMapping.put("Q_WF_MS_QUALITYCONTROL_RESULTS", "Mass Spectrometry Quality Control Results");
    namesMapping.put("Q_WF_NGS_EPITOPE_PREDICTION_LOGS", "Epitope Prediction Logs");
    namesMapping.put("Q_WF_NGS_EPITOPE_PREDICTION_RESULTS", "Epitope Prediction Results");
    namesMapping.put("Q_WF_NGS_HLATYPING_LOGS", "HLA Typing Workflow Logs");
    namesMapping.put("Q_WF_NGS_HLATYPING_RESULTS", "HLA Typing Workflow Results");
    namesMapping.put("Q_WF_NGS_QUALITYCONTROL_RESULTS", "NGS Quality Control Results");
    namesMapping.put("Q_WF_NGS_QUALITYCONTROL_LOGS", "NGS Quality Control Logs");
    namesMapping.put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_LOGS", "Gene Expression Analysis Logs");
    namesMapping.put("Q_WF_NGS_RNAEXPRESSIONANALYSIS_RESULTS", "Gene Expression Analysis Results");
    namesMapping.put("Q_WF_NGS_VARIANT_ANNOTATION_LOGS", "Variant Annotation Logs");
    namesMapping.put("Q_WF_NGS_VARIANT_ANNOTATION_RESULTS", "Variant Annotation Results");
    namesMapping.put("Q_WF_NGS_VARIANT_CALLING_LOGS", "Variant Calling Logs");
    namesMapping.put("Q_WF_NGS_VARIANT_CALLING_RESULTS", "Variant Calling Results");
    namesMapping.put("Q_WF_NGS_MAPPING_LOGS", "NGS Read Alignment Logs");
    namesMapping.put("Q_WF_NGS_MAPPING_RESULTS", "NGS Read Alignment Results");
    namesMapping.put("MZML", "Mass Spectrometry Raw Data (MZML)");
    namesMapping.put("Q_MS_MZML_DATA", "Mass Spectrometry Raw Data (MZML)");
    namesMapping.put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_RESULTS", "Individualized Proteins Results");
    namesMapping.put("Q_WF_MS_INDIVIDUALIZED_PROTEOME_LOGS", "Individualized Proteins Logs");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_QC_LOGS", "Ligandomics Quality Control Workflow Logs");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_QC_RESULTS",
        "Ligandomics Quality Control Workflow Results");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_ID_LOGS", "Ligandomics Identification Workflow Logs");
    namesMapping.put("Q_WF_MS_LIGANDOMICS_ID_RESULTS",
        "Ligandomics Identification Workflow Results");
    namesMapping.put("Q_WF_NGS_SHRNA_COUNTING_LOGS", "shRNA Counting Workflow Logs");
    namesMapping.put("Q_WF_NGS_SHRNA_COUNTING_RESULTS", "shRNA Counting Workflow Results");
    namesMapping.put("Q_WF_NGS_16S_TAXONOMIC_PROFILING_LOGS", "16S Taxonomic Profiling Logs");
    namesMapping.put("Q_NGS_READ_MATCH_ARCHIVE", "Read-Match Archive");
    namesMapping.put("Q_BMI_IMAGING_DATA", "Imaging Data");
    namesMapping.put("Q_PEPTIDE_DATA", "Peptide Data");
    namesMapping.put("Q_HT_QPCR_DATA", "High-Throughput qPCR Data");



    // deprecated dataset types
    namesMapping.put("ARR", "ARR Data (deprecated)");
    namesMapping.put("AUDIT", "AUDIT Data (deprecated)");
    namesMapping.put("BAI", "BAI Data (deprecated)");
    namesMapping.put("BAM", "BAM Data (deprecated)");
    namesMapping.put("CEL", "CEL Data (deprecated)");
    namesMapping.put("CSV", "CSV Data (deprecated)");
    namesMapping.put("EXPERIMENTAL_DESIGN", "Experimental Design Data (deprecated)");
    namesMapping.put("EXPRESSION_MATRIX", "Expression Data (deprecated)");
    namesMapping.put("FASTQ", "Fastq Data (deprecated)");
    namesMapping.put("FASTQC", "Fastq Data (deprecated)");
    namesMapping.put("FEATUREXML", "FeatureXML Data (deprecated)");
    namesMapping.put("GZ", "Gzipped Data (deprecated)");
    namesMapping.put("IDXML", "IDXML Data (deprecated)");
    namesMapping.put("JPG", "JPG Data (deprecated)");
    namesMapping.put("MAT", "MAT Data (deprecated)");
    namesMapping.put("PDF", "PDF Data (deprecated)");
    namesMapping.put("PNG", "PNG Data (deprecated)");
    namesMapping.put("PROT_RESULT", "Protein Data (deprecated)");
    namesMapping.put("RAW", "Mass Spectrometry Raw Data (deprecated)");
    namesMapping.put("SAM", "SAM Data (deprecated)");
    namesMapping.put("SHA256SUM", "Checksum (deprecated)");
    namesMapping.put("SRA", "SRA Data (deprecated)");
    namesMapping.put("TAR", "TAR Data (deprecated)");
    namesMapping.put("TSV", "TSV Data (deprecated)");
    namesMapping.put("UNKNOWN", "Type of Data unknown");
    namesMapping.put("VCF", "VCF Data (deprecated)");
    namesMapping.put("XSQ", "XSQ Data (deprecated)");
    namesMapping.put("ZIP", "ZIP Data (deprecated)");

    // Mulstiscale HCC specific stuff
    namesMapping.put("MSH_UNDEFINED_STATE", "Sample not yet part of the MultiscaleHCC workflow");
    namesMapping.put("MSH_SURGERY_SAMPLE_TAKEN", "Liver tumor biopsy finished");
    namesMapping.put("MSH_SENT_TO_PATHOLOGY", "Tumor sample sent to pathology");
    namesMapping.put("MSH_PATHOLOGY_REVIEW_STARTED", "Tumor sample is under review");
    namesMapping.put("MSH_PATHOLOGY_REVIEW_FINISHED", "Tumor sample review completed.");
    namesMapping
        .put("MSH_SENT_TO_HUMAN_GENETICS", "Tumor sample sent to Human Genetics department");

  }

  public String getPrettyName(String uglyName) {
    String prettyName = uglyName;

    if (namesMapping.containsKey(uglyName)) {
      prettyName = namesMapping.get(uglyName);
    }
    return prettyName;
  }

  public String getOpenBisName(String prettyName) {
    String uglyName = prettyName;

    if (namesMapping.containsValue(prettyName)) {
      uglyName = namesMapping.getKey(prettyName);
    }
    return uglyName;
  }

}
