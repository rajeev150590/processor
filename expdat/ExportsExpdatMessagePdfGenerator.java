/*
 * Copyright 2020 - Till Date BEO Softwares GmbH. All Rights Reserved.
 */
package com.beo.atlas.processor.writer.exports.expdat;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

import com.beo.atlas.rio.exports.dto.messages.expdat.ExportsExpdatMessageDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatHeaderDTO;
import com.beo.atlas.util.converter.GeneralCodeConverter;
import com.beo.atlas.util.logger.AtlasLogManager;
import com.beo.atlas.util.utility.StringUtils;
import com.itextpdf.text.Document;

/**
 * The Class ExportsExpdatMessagePdfGenerator.This is the class for Export Expdat Message Pdf Generator which is
 * responsible to process and render the info/pre-view side PDF of the Export Expdat Message.
 * 
 * @author ajeesh.mathew
 * @version 1.0
 * @since 23 June 2020
 */
public class ExportsExpdatMessagePdfGenerator extends ExportsExpdatMessagePdfGeneratorSubHelper
{

	/** The Constant LOGGER. */
	private static final AtlasLogManager LOGGER = new AtlasLogManager(ExportsExpdatMessagePdfGenerator.class);

	private transient ExportsExpdatMessageDTO exportsExpdatMessageDto = new ExportsExpdatMessageDTO();

	/**
	 * Instantiates a new export expdat message pdf generator.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @throws Exception the exception
	 */
	public ExportsExpdatMessagePdfGenerator() throws Exception
	{
		super();
	}

	/**
	 * Generate pdf for expdat in ByteArrayOutputStream format.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatMessageDTO the exports expdat message dto
	 * @param locale the locale
	 * @return the byte array output stream
	 */
	public ByteArrayOutputStream generatePDF(final ExportsExpdatMessageDTO exportsExpdatMessageDTO, final String locale)
	{
		final Document document = (Document) pdfCreator.getPdfDocumentObject();
		exportsExpdatMessageDto = exportsExpdatMessageDTO;
		try
		{
			pdfCreator.setMainHeading(StringUtils.getTrimValueAfterNullCheck(
					property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HEADER, locale)));
			if (null != exportsExpdatMessageDTO)
			{
				final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO =
						exportsExpdatMessageDTO.getExportsExpdatHeaderDto();
				addHeaderDetails(exportsExpdatHeaderDTO, locale);
				addHeaderTabDetails(exportsExpdatHeaderDTO, locale);
				addPositionDetails(exportsExpdatMessageDTO, locale);
			}
			pdfCreator.endProcess();
		}
		catch (final Exception exception)
		{
			document.close();
			LOGGER.error("Exception from ExportsExpdatMessagePdfGenerator method generatePDF ", exception);
		}
		return pdfCreator.getByteArrayOutputsteam();

	}

	/**
	 * Adds the header details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09,2021
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		contentMap = new LinkedHashMap<>();

		addHeaderNachrichtDetails(exportsExpdatHeaderDTO, locale);
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getArtderAnmeldungAusfuhr(),
				exportsExpdatHeaderDTO.getArtderAnmeldungVerfahren()))
		{
			addHeaderArtderAnmeldungDetails(exportsExpdatHeaderDTO, locale);
		}
		addHeaderAllgemeineAngabenDetails(exportsExpdatHeaderDTO, locale);
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getGestellungVerpackenVerladenAnfang(),
				exportsExpdatHeaderDTO.getGestellungVerpackenVerladenEnde()))
		{
			addHeaderGestellungDetails(exportsExpdatHeaderDTO, locale);
		}
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getBewilligungZugelassenerAusfuhrer(),
				exportsExpdatHeaderDTO.getBewilliAnschreibeverfahrenPassivenVeredelung(),
				exportsExpdatHeaderDTO.getBewilligungPassiveVeredelung()))
		{
			addHeaderBewilligungDetails(exportsExpdatHeaderDTO, locale);
		}
		addHeaderAngabenzumBeforderungsmittelDetails(exportsExpdatHeaderDTO, locale);
		addHeaderAngabenzudenDienststellenDetails(exportsExpdatHeaderDTO, locale);
		addHeaderDetailsSub(exportsExpdatHeaderDTO, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getIdentificationArt()))
		{
			addHeaderAusfuhrerAddressDetails(exportsExpdatHeaderDTO, locale);
			addHeaderAusfuhrerAnsprechpartnerAddressDetails(exportsExpdatHeaderDTO, locale);
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getIdentificationArt()))
		{
			addHeaderVerfahrensinhaberPvAddressDetails(exportsExpdatHeaderDTO, locale);
			addHeaderVerfahrensinhaberPvAnsprechpartnerAddressDetails(exportsExpdatHeaderDTO, locale);
		}
		addHeaderPassiveVeredelungPanelDetails(exportsExpdatHeaderDTO, locale);
		addHeaderVerschlussenPanelDetails(exportsExpdatHeaderDTO, locale);
		addHeaderAnzahlPositionenPanelDetails(exportsExpdatHeaderDTO, locale);

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
	}

	/**
	 * Adds the header details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09,2021
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderDetailsSub(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getGeschaftsvorgangArt(),
				exportsExpdatHeaderDTO.getGeschaftsvorgangRechnungspreis(),
				exportsExpdatHeaderDTO.getGeschaftsvorgangWahrung()))
		{
			addHeaderAngabenzudemGeschaftsvorgangDetails(exportsExpdatHeaderDTO, locale);
		}
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getLieferbedingungIncotermCode(),
				exportsExpdatHeaderDTO.getLieferbedingungTextTeil(), exportsExpdatHeaderDTO.getLieferbedingungOrt()))
		{
			addHeaderAngabenzurLieferbedingungDetails(exportsExpdatHeaderDTO, locale);
		}
		addHeaderWarenortLadeortDetails(exportsExpdatHeaderDTO, locale);

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

		contentMap.clear();

		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getIdentificationArt()))
		{
			addHeaderAnmelderAddressDetails(exportsExpdatHeaderDTO, locale);
			addHeaderAnmelderAnsprechpartnerAddressDetails(exportsExpdatHeaderDTO, locale);
		}
		addHeaderEmpfangerAddressDetails(exportsExpdatHeaderDTO, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getIdentificationArt()))
		{
			addHeaderVertreterDesAnmeldersAddressDetails(exportsExpdatHeaderDTO, locale);
			addHeaderVertreterDesAnmeldersAnsprechpartnerAddressDetails(exportsExpdatHeaderDTO, locale);
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getIdentificationArt()))
		{
			addHeaderSubunternehmerAddressDetails(exportsExpdatHeaderDTO, locale);
			addHeaderSubunternehmerAnsprechpartnerAddressDetails(exportsExpdatHeaderDTO, locale);
		}
	}

	/**
	 * Adds the header Art der Anmeldung details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderArtderAnmeldungDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ARTDERANMELDUNG, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getArtderAnmeldungAusfuhr()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ARTDERANMELDUNGAUSFUHR,
					exportsExpdatHeaderDTO.getArtderAnmeldungAusfuhr(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getArtderAnmeldungVerfahren()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ARTDERANMELDUNGVERFAHREN,
					exportsExpdatHeaderDTO.getArtderAnmeldungVerfahren(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header allgemeine angaben details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAllgemeineAngabenDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ALLGEMEINEANGABEN, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getKopfDatumdesAusgangs()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DATUMDESAUSGANGS,
					exportsExpdatHeaderDTO.getKopfDatumdesAusgangs(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getMassgeblichesDatum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_MASSGEBLICHESDATUM,
					exportsExpdatHeaderDTO.getMassgeblichesDatum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVermerk()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERMERK,
					exportsExpdatHeaderDTO.getVermerk(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getMrn()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_MRN,
					exportsExpdatHeaderDTO.getMrn(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getContainer()))
		{
			if ("1".equals(exportsExpdatHeaderDTO.getContainer()))
			{
				addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_CONTAINER,
						"1 - ja", locale);
				index++;
			}
			else
			{
				addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_CONTAINER,
						"0 - nein", locale);
				index++;

			}
		}
		addHeaderAllgemeineAngabenSubDetails(exportsExpdatHeaderDTO, locale);

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header allgemeine angaben remaining details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAllgemeineAngabenSubDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale)
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getRegistriernummerFremdsystem()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REGISTRIERNUMMERFREMDSYSTEM,
					exportsExpdatHeaderDTO.getRegistriernummerFremdsystem(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBezugsnummer()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEZUGSNUMMER,
					exportsExpdatHeaderDTO.getBezugsnummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getKennnummerderSendung()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNNUMMERDERSENDUNG,
					exportsExpdatHeaderDTO.getKennnummerderSendung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBefrderungskostenZahlungsweise()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEFORDERUNGSKOSTENZAHLUNGSWEISE,
					exportsExpdatHeaderDTO.getBefrderungskostenZahlungsweise(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBesondereUmstande()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BESONDEREUMSTANDE,
					exportsExpdatHeaderDTO.getBesondereUmstande(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGesamtRohmasse()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GESAMTROHMASSE,
					exportsExpdatHeaderDTO.getGesamtRohmasse(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeteiligtenKonstellation()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BETEILIGTENKONSTELLATION,
					exportsExpdatHeaderDTO.getBeteiligtenKonstellation(), locale);
			index++;
		}
	}

	/**
	 * Adds the header gestellung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderGestellungDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZURGESTELLUNG, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGestellungVerpackenVerladenAnfang()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERPACKENUNDVERLADENANFANG,
					exportsExpdatHeaderDTO.getGestellungVerpackenVerladenAnfang(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGestellungVerpackenVerladenEnde()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERPACKENUNDVERLADENENDE,
					exportsExpdatHeaderDTO.getGestellungVerpackenVerladenEnde(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header bewilligung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderBewilligungDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEWILLIGUNG, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBewilligungZugelassenerAusfuhrer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZUGELASSENERAUSFUHRER,
					exportsExpdatHeaderDTO.getBewilligungZugelassenerAusfuhrer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBewilliAnschreibeverfahrenPassivenVeredelung()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSCHREIBEVERFAHRENINDERPASSIVENVEREDELUNG,
					exportsExpdatHeaderDTO.getBewilliAnschreibeverfahrenPassivenVeredelung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBewilligungPassiveVeredelung()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PASSIVEVEREDELUNG,
					exportsExpdatHeaderDTO.getBewilligungPassiveVeredelung(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header angabenzum beforderungsmittel details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAngabenzumBeforderungsmittelDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZUMBEFORDERUNGSMITTEL,
				false, ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelImInlandVerkehrszweig()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERKEHRSZWEIGIMINLAND,
					exportsExpdatHeaderDTO.getBeforderungsmittelImInlandVerkehrszweig(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelAbgangArt()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ARTDESBEFORDERUNGSMITTELSAMABGANG,
					exportsExpdatHeaderDTO.getBeforderungsmittelAbgangArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelAbgangKennzeichen()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHENDESBEFORDERUNGSMITTELSAMABGANG,
					exportsExpdatHeaderDTO.getBeforderungsmittelAbgangKennzeichen(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelAbgangStaatszugehorigkeit()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STAATSZUGEHORIGKEITDESBEFORDERUNGSMITTELAMABGANG,
					exportsExpdatHeaderDTO.getBeforderungsmittelAbgangStaatszugehorigkeit(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeVerkehrszweig()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERKEHRSZWEIGDESBEFORDERUNGSMITTELANDERGRENZE,
					exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeVerkehrszweig(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeArt()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ARTDESBEFORDERUNGSMITTELANDERGRENZE,
					exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeKennzeichen()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHENDESBEFORDERUNGSMITTELANDERGRENZE,
					exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeKennzeichen(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeStaatszugehorigkeit()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STAATSZUGEHORIGKEITDESBEFORDERUNGSMITTELANDERGRENZE,
					exportsExpdatHeaderDTO.getBeforderungsmittelderGrenzeStaatszugehorigkeit(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header angabenzuden dienststellen details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAngabenzudenDienststellenDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZURDENDIENSTSTELLEN, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrzollstelleDienststellennummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DIENSTSTELLENNUMMERDERAUSFUHRZOLLSTELLE,
					exportsExpdatHeaderDTO.getAusfuhrzollstelleDienststellennummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrzollstellefurdieeAMDienststellennummer()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DIENSTSTELLENNUMMERFURDIEEAMDERAUSFUHRZOLLSTELLE,
					exportsExpdatHeaderDTO.getAusfuhrzollstellefurdieeAMDienststellennummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVorgeseheneAusgangszollstelleDienststellennummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VORGESEHENEDIENSTSTELLENNUMMERDERAUSGANGSZOLLSTELLE,
					exportsExpdatHeaderDTO.getVorgeseheneAusgangszollstelleDienststellennummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getTatsachlicheAusgangszollstelleDienststellennummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TATSACHLICHEDIENSTSTELLENNUMMERDERAUSGANGSZOLLSTELLE,
					exportsExpdatHeaderDTO.getTatsachlicheAusgangszollstelleDienststellennummer(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header angabenzudem geschaftsvorgang details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAngabenzudemGeschaftsvorgangDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZUDEMGESCHAFTSVORGANG,
				false, ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGeschaftsvorgangArt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ART,
					exportsExpdatHeaderDTO.getGeschaftsvorgangArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGeschaftsvorgangRechnungspreis()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_RECHNUNGSPREIS,
					exportsExpdatHeaderDTO.getGeschaftsvorgangRechnungspreis(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getGeschaftsvorgangWahrung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WAHRUNG,
					exportsExpdatHeaderDTO.getGeschaftsvorgangWahrung(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header angabenzur lieferbedingung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAngabenzurLieferbedingungDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZURLIEFERBEDINGUNG, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getLieferbedingungIncotermCode()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_INCOTERMCODE,
					exportsExpdatHeaderDTO.getLieferbedingungIncotermCode(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getLieferbedingungTextTeil()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TEXTTEIL,
					exportsExpdatHeaderDTO.getLieferbedingungTextTeil(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getLieferbedingungOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getLieferbedingungOrt(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header warenortLadeort details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderWarenortLadeortDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getWarenortLadeortCode()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENORTLADEORT, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_CODE,
					exportsExpdatHeaderDTO.getWarenortLadeortCode(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getWarenortLadeortStrassHausnummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getWarenortLadeortStrassHausnummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getWarenortLadeortplz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getWarenortLadeortplz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getWarenortLadeortOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getWarenortLadeortOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getWarenortLadeortZusatz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZUSATZ,
					exportsExpdatHeaderDTO.getWarenortLadeortZusatz(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header Nachricht details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy amalchand
	 * @modifiedDate 01 Sep 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderNachrichtDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HEADER_NACHRICHT, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getNachrichtennummer()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NACHRICHTENNUMMER,
					exportsExpdatHeaderDTO.getNachrichtennummer(), locale);
			index++;
		}
		addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NACHRICHTENGRUPPE,
				GeneralCodeConverter.htmlConverter(StringUtils.getTrimValueAfterNullCheck(property
						.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NACHRICHTENGRUPPE_VALUE, locale))),
				locale);
		index++;
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEoriNiederlassungsnummer()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EORINIEDERLASSUNGSNUMMER,
					getEoriFromEoriBinNumber(exportsExpdatHeaderDTO.getEoriNiederlassungsnummer()), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatMessageDto.getSzenarioNummer()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SZENARIONUMMER,
					exportsExpdatMessageDto.getSzenarioNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatMessageDto.getSzenarioHinweis()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SZENARIOHINWEIS,
					exportsExpdatMessageDto.getSzenarioHinweis(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getZeitpunktderAnmeldung()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZEITPUNKTDERANMELDUNG,
					exportsExpdatHeaderDTO.getZeitpunktderAnmeldung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getDienststellennummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DIENSTSTELLENNUMMER,
					exportsExpdatHeaderDTO.getDienststellennummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrland()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_AUSFUHRLAND,
					exportsExpdatHeaderDTO.getAusfuhrland(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getBestimmungsLand()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BESTIMMUNGSLAND,
					exportsExpdatHeaderDTO.getBestimmungsLand(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header anmelder address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAnmelderAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANMELDER, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getAnmelder().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getTin()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getAnmelder().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getNiederlassungsNummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getAnmelder().getNiederlassungsNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatHeaderDTO.getAnmelder().getName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getStrasse()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getAnmelder().getStrasse(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getAnmelder().getOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getPlz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getAnmelder().getPlz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getLand()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatHeaderDTO.getAnmelder().getLand(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header anmelder ansprechpartner address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderAnmelderAnsprechpartnerAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSPRECHPARTNER, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getAnsprechStellung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STELLUNG,
					exportsExpdatHeaderDTO.getAnmelder().getAnsprechStellung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getAnsprechName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SACHBEARBEITER,
					exportsExpdatHeaderDTO.getAnmelder().getAnsprechName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getPhone()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFONNUMMER,
					exportsExpdatHeaderDTO.getAnmelder().getPhone(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getAnsprechTelefax()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFAXNUMMER,
					exportsExpdatHeaderDTO.getAnmelder().getAnsprechTelefax(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getAnsprechEmail()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMAILADRESSE,
					exportsExpdatHeaderDTO.getAnmelder().getAnsprechEmail(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getReferenceNum()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENCENAME,
					exportsExpdatHeaderDTO.getAnmelder().getReferenceNum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatHeaderDTO.getAnmelder().getTinNeuValue(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnmelder().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatHeaderDTO.getAnmelder().getTinNeuValue(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header empfanger address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderEmpfangerAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMPFANGER, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getEmpfanger().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getTin()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getEmpfanger().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getNiederlassungsNummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getEmpfanger().getNiederlassungsNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatHeaderDTO.getEmpfanger().getName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getStrasse()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getEmpfanger().getStrasse(), locale);
			index++;
		}
		addHeaderEmpfangerAddressSubDetails(exportsExpdatHeaderDTO, locale);

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header empfanger address remaining details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderEmpfangerAddressSubDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale)
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getEmpfanger().getOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getPlz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getEmpfanger().getPlz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getLand()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatHeaderDTO.getEmpfanger().getLand(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getKundenNum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KUNDENNUMMER,
					exportsExpdatHeaderDTO.getEmpfanger().getKundenNum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatHeaderDTO.getEmpfanger().getTinNeuValue(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getEmpfanger().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatHeaderDTO.getEmpfanger().getTinNeuValue(), locale);
			index++;
		}
	}

}
