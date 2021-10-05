/*
 * Copyright 2020 - Till Date BEO Softwares GmbH. All Rights Reserved.
 */
package com.beo.atlas.processor.writer.exports.expdat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.beo.atlas.processor.writer.exports.ExportsPdfGeneratorHelper;
import com.beo.atlas.processor.writer.exports.expamd.ExportsExpamdMessagePdfConstants;
import com.beo.atlas.rio.exports.dto.messages.expdat.ExportsExpdatMessageDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatBeendigungAvuvPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatBeendigungZlPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatContainerPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatPackstuckPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatPositionDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatUnterlagePositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatVorpapierPositionTabDTO;
import com.beo.atlas.util.utility.StringUtils;

/**
 * The Class ExportsExpdatMessagePdfGeneratorHelper.This is the class for Export Expdat Message Pdf Generator which is
 * responsible to process and render the info/pre-view side PDF of the Export Expdat Message.
 * 
 * @author ajeesh.mathew
 * @version 1.0
 * @since 23 June 2020
 */
public class ExportsExpdatMessagePdfGeneratorHelper extends ExportsPdfGeneratorHelper
{

	/** The index. */
	public transient int index = ExportsExpdatMessagePdfConstants.KEY_INDEX_1;

	/**
	 * Instantiates a new export expdat message pdf generator helper.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 */
	public ExportsExpdatMessagePdfGeneratorHelper() throws Exception
	{
		super();
	}

	/**
	 * Adds the position details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatMessageDTO the exports expdat message dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addPositionDetails(final ExportsExpdatMessageDTO exportsExpdatMessageDTO, final String locale)
			throws Exception
	{
		final List<ExportsExpdatPositionDTO> exportsExpdatPositionDTOList =
				exportsExpdatMessageDTO.getExportsExpdatPositionDto();

		if (null != exportsExpdatPositionDTOList && !exportsExpdatPositionDTOList.isEmpty())
		{
			contentMap = new LinkedHashMap<>();
			pdfCreator.setMainHeading(StringUtils.getTrimValueAfterNullCheck(
					property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale)));
			int positionIndex = ExportsExpdatMessagePdfConstants.KEY_INDEX_1;
			for (final ExportsExpdatPositionDTO exportsExpdatPositionDTO : exportsExpdatPositionDTOList)
			{
				contentMap.clear();
				addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITIONSSEITE, false,
						ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITIONSNUMMER,
						exportsExpdatPositionDTO.getWarePositionsnummer(), locale);
				positionIndex++;
				addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
						ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
				contentMap.clear();

				addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENDETAILS, false,
						ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarennummerKN8()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENNUMMERKN8,
							exportsExpdatPositionDTO.getWareWarennummerKN8(), locale);
					positionIndex++;
				}
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarenbezeichnung()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EXPDATPOSITION_WARENBEZEICHNUNG,
							exportsExpdatPositionDTO.getWareWarenbezeichnung(), locale);
					positionIndex++;
				}
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareRegistriernummerFremdsystem()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REGISTRIERFREMDSYSTEM,
							exportsExpdatPositionDTO.getWareRegistriernummerFremdsystem(), locale);
					positionIndex++;
				}
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareVermerk()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WAREVERMERK,
							exportsExpdatPositionDTO.getWareVermerk(), locale);
					positionIndex++;
				}
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarennummerTaric()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TARIC,
							exportsExpdatPositionDTO.getWareWarennummerTaric(), locale);
					positionIndex++;
				}
				if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarennummerErsterZusatz()))
				{
					addStringArrayToMapForSingleFields(positionIndex,
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TARICEZTERSTERZUSATZ,
							exportsExpdatPositionDTO.getWareWarennummerErsterZusatz(), locale);
					positionIndex++;
				}
				positionIndex = addPositionDetailsSub(locale, positionIndex, exportsExpdatPositionDTO);
				addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
						ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
				contentMap.clear();

				positionIndex = addPositionTabDetails(locale, positionIndex, exportsExpdatPositionDTO);

			}
		}
	}

	/**
	 * Adds the position remaining details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatMessageDTO the exports expdat message dto
	 * @param positionIndex the position index
	 * @param locale the locale
	 * @return the int
	 */
	private int addPositionDetailsSub(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO)
	{

		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarennummerZweiterZusatz()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TARICEZTZWEITERZUSATZ,
					exportsExpdatPositionDTO.getWareWarennummerZweiterZusatz(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareWarennummerNationaleAngaben()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NATIONALEANGABEN,
					exportsExpdatPositionDTO.getWareWarennummerNationaleAngaben(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareEigenmasse()))
		{
			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EIGENMASSE,
					exportsExpdatPositionDTO.getWareEigenmasse(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareRohmasse()))
		{
			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ROHMASSE,
					exportsExpdatPositionDTO.getWareRohmasse(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareUrsprungsbundesland()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_URSPRUNGSBUNDESLAND,
					exportsExpdatPositionDTO.getWareUrsprungsbundesland(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareGefahrgutnummerUndg()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GEFAHRGUTNUMMERUNDG,
					exportsExpdatPositionDTO.getWareGefahrgutnummerUndg(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareBeforderungskostenZahlungsweise()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EXPDATPOSITION_BEFORDERUNGSKOSTENZAHLUNGSWEISE,
					exportsExpdatPositionDTO.getWareBeforderungskostenZahlungsweise(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getWareKennnummerSendung()))
		{
			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNUMMERDERSENDUNG,
					exportsExpdatPositionDTO.getWareKennnummerSendung(), locale);
			positionIndex++;
		}
		return positionIndex;
	}

	/**
	 * Adds the position emfanger details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionEmfangerDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMPFANGER, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatPositionDTO.getEmpfanger().getIdentificationArt(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getTin()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatPositionDTO.getEmpfanger().getTin(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getNiederlassungsNummer()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatPositionDTO.getEmpfanger().getNiederlassungsNummer(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getName()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatPositionDTO.getEmpfanger().getName(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getStrasse()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatPositionDTO.getEmpfanger().getStrasse(), locale);
			positionIndex++;
		}
		positionIndex = addPositionEmfangerDetailsSub(locale, positionIndex, exportsExpdatPositionDTO);

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
		return positionIndex;
	}

	/**
	 * Adds the position emfanger details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 */
	private int addPositionEmfangerDetailsSub(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO)
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getOrt()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatPositionDTO.getEmpfanger().getOrt(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getPlz()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatPositionDTO.getEmpfanger().getPlz(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getLand()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatPositionDTO.getEmpfanger().getLand(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getKundenNum()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KUNDENNUMMER,
					exportsExpdatPositionDTO.getEmpfanger().getKundenNum(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getTinNeuValue()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatPositionDTO.getEmpfanger().getTinNeuValue(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getEmpfanger().getTinNeuValue()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatPositionDTO.getEmpfanger().getTinNeuValue(), locale);
			positionIndex++;
		}
		return positionIndex;
	}

	/**
	 * Adds the position geschaftsvorgang details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionGeschaftsvorgangDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getGeschaftsvorgangArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GESCHAFTSVORGANG, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ART,
					exportsExpdatPositionDTO.getGeschaftsvorgangArt(), locale);
			positionIndex++;

			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;
	}

	/**
	 * Adds the position aussenhandels statistik details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09 2021
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionAussenhandelsstatistikDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		if (StringUtils.needToWrite(exportsExpdatPositionDTO.getAussenhandelsstatistikMenge()
				,exportsExpdatPositionDTO.getAussenhandelsstatistikWert()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_AUSSENHANDELSSTATISTIK, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getAussenhandelsstatistikMenge()))
			{
				addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_MENGE,
						exportsExpdatPositionDTO.getAussenhandelsstatistikMenge(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getAussenhandelsstatistikWert()))
			{
				addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WERT,
						exportsExpdatPositionDTO.getAussenhandelsstatistikWert(), locale);
				positionIndex++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;
	}

	/**
	 * Adds the position Ausfuhrerstattung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionAusfuhrerstattungDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getAusfuhrerstattungMenge()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DATAGROUP, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_AUSFUHRERSTATTUNG_MENGE,
					exportsExpdatPositionDTO.getAusfuhrerstattungMenge(), locale);
			positionIndex++;

			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;
	}

	/**
	 * Adds the position tab's details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 13 July 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionTabDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		positionIndex = addPositionEmfangerDetails(locale, positionIndex, exportsExpdatPositionDTO);

		positionIndex = addPositionVerfahrenDetails(locale, positionIndex, exportsExpdatPositionDTO);

		positionIndex = addPositionLieferbedingungDetails(locale, positionIndex, exportsExpdatPositionDTO);

		positionIndex = addPositionGeschaftsvorgangDetails(locale, positionIndex, exportsExpdatPositionDTO);

		positionIndex = addPositionAussenhandelsstatistikDetails(locale, positionIndex, exportsExpdatPositionDTO);

		positionIndex = addPositionAusfuhrerstattungDetails(locale, positionIndex, exportsExpdatPositionDTO);

		addPositionTabPackstuckDetails(locale, exportsExpdatPositionDTO);

		addPositionTabContainerDetails(locale, exportsExpdatPositionDTO);

		addPositionTabUnterlageDetails(locale, exportsExpdatPositionDTO);

		contentMap.clear();

		positionIndex = addPositionTabZlDetailsHeader(locale, positionIndex, exportsExpdatPositionDTO);

		addPositionTabZlDetails(locale, exportsExpdatPositionDTO);

		contentMap.clear();

		positionIndex = addPositionTabAvuvDetailsHeader(locale, positionIndex, exportsExpdatPositionDTO);

		addPositionTabAvuvDetails(locale, exportsExpdatPositionDTO);

		addPositionTabVorpapierDetails(locale, exportsExpdatPositionDTO);

		contentMap.clear();

		return positionIndex;
	}

	/**
	 * Adds the position tab packstuck details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 09 Feb 2021
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabPackstuckDetails(final String locale,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatPackstuckPositionTabDTO> packstukeDtoList =
				exportsExpdatPositionDTO.getPackstuckPositionTabDto();
		if (null != packstukeDtoList && !packstukeDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ANZAHL, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERPACKUNGSART, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HAUPTPACK, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZEICHENNUMMERN, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatPackstuckPositionTabDTO packstukeDto : packstukeDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(packstukeDto.getPackstuckNummer()),
						StringUtils.getTrimValueAfterNullCheck(packstukeDto.getPackstuckAnzahl()),
						StringUtils.getTrimValueAfterNullCheck(getReceiveMessageOptionsCodeDescription(
								packstukeDto.getPackstuckVerpackungsart(), ExportsExpamdMessagePdfConstants.C0017,
								ExportsExpamdMessagePdfConstants.AES, locale)),
						StringUtils.getTrimValueAfterNullCheck(packstukeDto.getVerpacktInHauptpackPositionsNr()),
						StringUtils.getTrimValueAfterNullCheck(packstukeDto.getPackstuckZeichenNummern()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PACKSTUCK, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_5, locale);
		}
	}
	/**
	 * Gets the receive message options code description.
	 * 
	 * @author ajeesh.mathew@beo.in
	 * @modifiedBy
	 * @modifiedDate Feb 09,2021
	 * 
	 * @param code the code
	 * @param type the type
	 * @param module the module
	 * @return the receive message options code description
	 */
	private String getReceiveMessageOptionsCodeDescription(final String code, final String type, final String module,
			final String local)
	{

		if (Objects.nonNull(this.receivingMsgOptionCacheFacedRemote) && StringUtils.isNotNullOrEmpty(code))
		{
			return this.receivingMsgOptionCacheFacedRemote.getReceiveOptionDetailCache(code, type, module, local);
		}
		return ExportsExpamdMessagePdfConstants.EMPTY_STRING;
	}

	/**
	 * Adds the position container tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabContainerDetails(final String locale,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatContainerPositionTabDTO> containerDtoList =
				exportsExpdatPositionDTO.getContainerPositionTabDto();
		if (null != containerDtoList && !containerDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NUMMER, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatContainerPositionTabDTO containerDto : containerDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(containerDto.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(containerDto.getContainerNumber()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_CONTAINER, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		}
	}

	/**
	 * Adds the position tab unterlage details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabUnterlageDetails(final String locale,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatUnterlagePositionTabDTO> unterlageDtoList =
				exportsExpdatPositionDTO.getUnterlagePositionTabDto();
		if (null != unterlageDtoList && !unterlageDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TYP, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_QUALIFIKATOR, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENZ, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ZUSATZ, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DETAIL, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DATUMDERAUSSTELLUNG,
							locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DATUMDESGULTIGKEITSENDES,
							locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WERT, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_MASSEINHEIT, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ABSCHREIBUNGSMENGE,
							locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatUnterlagePositionTabDTO unterlageDto : unterlageDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlage()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageType()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageQualifikator()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageReferenz()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageZusatz()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageDetail()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageDatumderAusstellung()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageDatumGultigkeitsendes()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageWert()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageMasseinheit()),
						StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageAbschreibungsmenge()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_UNTERLAGE, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_11, locale);
		}
	}

	/**
	 * Adds the position zl tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabZlDetails(final String locale, final ExportsExpdatPositionDTO exportsExpdatPositionDTO)
			throws Exception
	{
		final List<ExportsExpdatBeendigungZlPositionTabDTO> tabZlDtoList =
				exportsExpdatPositionDTO.getBeendigungZlPositionTabDto();
		if (null != tabZlDtoList && !tabZlDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SATZNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITIONSNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REGISTRIERNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_WARENNUMMER,
							locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHENZUGANGINATLAS,
							locale),
					this.property.getProperty(
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHENUBLICHEBEHANDLUNG, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZUSATZ, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ABGANGSMENGEWERT, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ABGANGSMENGEWERT_LABEL,
							locale),
					this.property.getProperty(
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ABGANGSMENGEQUALIFIKATOR_LABEL, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HANDELSMENGEWERT_LABEL,
							locale),
					this.property.getProperty(
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HANDELSMENGEMASSEINHEIT_LABEL, locale),
					this.property.getProperty(
							ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_HANDELSMENGEQUALIFIKATOR_LABEL, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatBeendigungZlPositionTabDTO tabZlDto : tabZlDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZL()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLSatznummer()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLPositionsnummer()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLRegistriernummer()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLWarennummer()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLKennzeichenZuganInAtlas()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositionZLKennzeichenUblicheBehandlung()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getPositioZlZusatz()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getAbgangsmengesWert()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getAbgangsmengeMasseinheit()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getAbgangsmengeQualifikator()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getHandelsmengeWert()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getHandelsmengeMasseinheit()),
						StringUtils.getTrimValueAfterNullCheck(tabZlDto.getHandelsmengesQualifikator()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_14, locale);
		}
	}

	/**
	 * Adds the position zl tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 13 July 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private int addPositionTabZlDetailsHeader(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatBeendigungZlPositionTabDTO> tabZlDtoList =
				exportsExpdatPositionDTO.getBeendigungZlPositionTabDto();
		if (null != tabZlDtoList && !tabZlDtoList.isEmpty())
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEENDIGUNGZL, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getBeendigungZLAnzahlPositionen()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ANZAHLPOSITIONEN,
						exportsExpdatPositionDTO.getBeendigungZLAnzahlPositionen(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getBeendigungZLBewilligungsnummer()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEWILLIGUNGSNUMMER,
						exportsExpdatPositionDTO.getBeendigungZLBewilligungsnummer(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getBeendigungZLBezugsnummer()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEEDIGUNGZLBEZUGNR,
						exportsExpdatPositionDTO.getBeendigungZLBezugsnummer(), locale);
				positionIndex++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;

	}

	/**
	 * Adds the position avuv tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabAvuvDetails(final String locale, final ExportsExpdatPositionDTO exportsExpdatPositionDTO)
			throws Exception
	{
		final List<ExportsExpdatBeendigungAvuvPositionTabDTO> tabAvuvDtoList =
				exportsExpdatPositionDTO.getBeendigungavuvPositionTabDto();
		if (null != tabAvuvDtoList && !tabAvuvDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITIONAVUV, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SATZNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITIONSNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REGISTRIERNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHENZUGANGINATLAS,
							locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENBEZOGENEANGABEN,
							locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatBeendigungAvuvPositionTabDTO tabAvuvDto : tabAvuvDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuv()),
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuvSatznummer()),
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuvPositionsnummer()),
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuvRegistriernummer()),
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuvKennzeichenZugangAtlas()),
						StringUtils.getTrimValueAfterNullCheck(tabAvuvDto.getPositionAvuvWarenbezogeneAngaben()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_6, locale);
		}
	}

	/**
	 * Adds the position avuv tab header details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 13 July 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private int addPositionTabAvuvDetailsHeader(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatBeendigungAvuvPositionTabDTO> tabAvuvDtoList =
				exportsExpdatPositionDTO.getBeendigungavuvPositionTabDto();
		if (null != tabAvuvDtoList && !tabAvuvDtoList.isEmpty())
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEENDIGUNGAVUV, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getBeendigungAvuvAnzahlPositionen()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ANZAHLPOSITIONEN,
						exportsExpdatPositionDTO.getBeendigungAvuvAnzahlPositionen(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getBeendigungAvuvBewilligungsnummer()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEWILLIGUNGSNUMMER,
						exportsExpdatPositionDTO.getBeendigungAvuvBewilligungsnummer(), locale);
				positionIndex++;
			}

			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;
	}

	/**
	 * Adds the position vorpapier tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @throws Exception the exception
	 */
	private void addPositionTabVorpapierDetails(final String locale,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		final List<ExportsExpdatVorpapierPositionTabDTO> vorpapierPositionTabDtoList =
				exportsExpdatPositionDTO.getVorpapierPositionTabDto();

		if (null != vorpapierPositionTabDtoList && !vorpapierPositionTabDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TYP, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENZ, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_ZUSATZ, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatVorpapierPositionTabDTO vorpapierPositionTabDto : vorpapierPositionTabDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(vorpapierPositionTabDto.getVorpapier()),
						StringUtils.getTrimValueAfterNullCheck(vorpapierPositionTabDto.getVorpapierType()),
						StringUtils.getTrimValueAfterNullCheck(vorpapierPositionTabDto.getVorpapierReferenz()),
						StringUtils.getTrimValueAfterNullCheck(vorpapierPositionTabDto.getVorpapierZusatz()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VORPAPIER, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_4, locale);
		}
	}

	/**
	 * Adds the position lieferbedingung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09,2021
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionLieferbedingungDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		if (StringUtils.needToWrite(exportsExpdatPositionDTO.getLieferbedingungIncotermCode()
				,exportsExpdatPositionDTO.getLieferbedingungText()
				,exportsExpdatPositionDTO.getLieferbedingungOrt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LIEFERBEDINGUNG, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getLieferbedingungIncotermCode()))
			{
				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION_INCOTERMCODE,
						exportsExpdatPositionDTO.getLieferbedingungIncotermCode(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getLieferbedingungText()))
			{

				addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TEXT,
						exportsExpdatPositionDTO.getLieferbedingungText(), locale);
				positionIndex++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getLieferbedingungOrt()))
			{

				addStringArrayToMapForSingleFields(positionIndex,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EXPDATPOSITION_ORT,
						exportsExpdatPositionDTO.getLieferbedingungOrt(), locale);
				positionIndex++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
		return positionIndex;
	}

	/**
	 * Adds the position verfahren details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param locale the locale
	 * @param positionIndex the position index
	 * @param exportsExpdatPositionDTO the exports expdat position dto
	 * @return the int
	 * @throws Exception the exception
	 */
	private int addPositionVerfahrenDetails(final String locale, int positionIndex,
			final ExportsExpdatPositionDTO exportsExpdatPositionDTO) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERFAHREN, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getVerfahrenAngemeldetes()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGEMELDETES,
					exportsExpdatPositionDTO.getVerfahrenAngemeldetes(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getVerfahrenVorangegangenes()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VORANGEGANGENES,
					exportsExpdatPositionDTO.getVerfahrenVorangegangenes(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getVerfahrenWeiteres()))
		{

			addStringArrayToMapForSingleFields(positionIndex, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WEITERES,
					exportsExpdatPositionDTO.getVerfahrenWeiteres(), locale);
			positionIndex++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatPositionDTO.getVerfahrenAusfuhrerstattung()))
		{

			addStringArrayToMapForSingleFields(positionIndex,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_AUSFUHRERSTATTUNG,
					exportsExpdatPositionDTO.getVerfahrenAusfuhrerstattung(), locale);
			positionIndex++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
		return positionIndex;

	}

}
