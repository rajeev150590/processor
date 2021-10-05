/*
 * Copyright 2020 - Till Date BEO Softwares GmbH. All Rights Reserved.
 */
package com.beo.atlas.processor.writer.exports.expdat;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatBeforderungsrouteHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatBesondererHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatErzeugnisHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatHeaderDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatNamlichkeitsmittelHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatVerschlusseZeichenHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatWiedereinfuhrHeaderTabDTO;
import com.beo.atlas.util.utility.StringUtils;

/**
 * The Class ExportsExpdatMessagePdfGeneratorSubHelper.This is the class for Export Expdat Message Pdf Generator which
 * is responsible to process and render the info/pre-view side PDF of the Export Expdat Message.
 * 
 * @author ajeesh.mathew
 * @version 1.0
 * @since 23 June 2020
 */
public class ExportsExpdatMessagePdfGeneratorSubHelper extends ExportsExpdatMessagePdfGeneratorHelper
{

	/**
	 * Instantiates a new export expdat message pdf generator sub helper.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 */
	public ExportsExpdatMessagePdfGeneratorSubHelper() throws Exception
	{
		super();
	}

	/**
	 * Adds the header tab details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 08 July 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderTabDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		final List<ExportsExpdatBesondererHeaderTabDTO> besondererDtoList =
				exportsExpdatHeaderDTO.getBesondererHeaderTabDto();
		if (null != besondererDtoList && !besondererDtoList.isEmpty())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_KENNZEICHEN, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatBesondererHeaderTabDTO besondererDTO : besondererDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(besondererDTO.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(besondererDTO.getBesondererTatbestandKennzeichen()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BESONDERERTATBESTAND, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		}

		final List<ExportsExpdatVerschlusseZeichenHeaderTabDTO> verschlusseZeichenDtoList =
				exportsExpdatHeaderDTO.getVerschlusseZeichenHeaderTabDto();
		if (null != verschlusseZeichenDtoList && !verschlusseZeichenDtoList.isEmpty())
		{

			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ZEICHEN, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatVerschlusseZeichenHeaderTabDTO verschlusseZeichenDTO : verschlusseZeichenDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(verschlusseZeichenDTO.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(verschlusseZeichenDTO.getVerschlusseZeichen()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERSCHLUSSEZEICHENZ, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		}

		if (null != exportsExpdatHeaderDTO.getBeforderungsrouteHeaderTabDto())
		{
			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			final List<String> beforderungsrouteLandList = createBeforderungsrouteLandList(exportsExpdatHeaderDTO);
			int position = 0;
			for (final String land : beforderungsrouteLandList)
			{
				position = position + 1;
				addStringArrayToMapForTabs(indexForTabs, String.valueOf(position),
						StringUtils.getTrimValueAfterNullCheck(land));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_BEFORDERUNGSROUTE, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		}

		addHeaderTabDetailsSub(exportsExpdatHeaderDTO, locale);

	}

	/**
	 * Creates the beforderungsroute land list for displaying expdat header info pdf apge.
	 *
	 * @author ajeesh.mathew@beo.in
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate July 08, 2020
	 * 
	 * @param exportsExpdatHeaderDto the exports expdat header dto
	 * @return the land list
	 */
	public List<String> createBeforderungsrouteLandList(final ExportsExpdatHeaderDTO exportsExpdatHeaderDto)
	{
		final List<String> beforderungsrouteLandList = new LinkedList<>();
		if (Objects.nonNull(exportsExpdatHeaderDto)
				&& Objects.nonNull(exportsExpdatHeaderDto.getBeforderungsrouteHeaderTabDto()))
		{
			final ExportsExpdatBeforderungsrouteHeaderTabDTO beforderungsrouteHeaderTabDto =
					exportsExpdatHeaderDto.getBeforderungsrouteHeaderTabDto();

			final String beforderungsVon = beforderungsrouteHeaderTabDto.getBeforderungsVon();
			if (StringUtils.isNotNullOrEmpty(beforderungsVon))
			{
				beforderungsrouteLandList.add(beforderungsVon);
			}
			final List<String> ausgewahlteLanderList = beforderungsrouteHeaderTabDto.getAusgewahlteLander();
			if (Objects.nonNull(ausgewahlteLanderList) && !ausgewahlteLanderList.isEmpty())
			{
				beforderungsrouteLandList.addAll(ausgewahlteLanderList);
			}

			final String beforderungsBis = beforderungsrouteHeaderTabDto.getBeforderungsBis();
			if (StringUtils.isNotNullOrEmpty(beforderungsBis))
			{
				beforderungsrouteLandList.add(beforderungsBis);
			}
		}
		return beforderungsrouteLandList;
	}

	/**
	 * Adds the header tab details in PDF.
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 08 July 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	private void addHeaderTabDetailsSub(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale)
			throws Exception
	{
		final List<ExportsExpdatWiedereinfuhrHeaderTabDTO> wiedereinfuhrDtoList =
				exportsExpdatHeaderDTO.getWiedereinfuhrHeaderTabDto();
		if (null != wiedereinfuhrDtoList && !wiedereinfuhrDtoList.isEmpty())
		{

			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WIEDEREINFUHRLAND,
							locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatWiedereinfuhrHeaderTabDTO wiedereinfuhrDTO : wiedereinfuhrDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(wiedereinfuhrDTO.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(wiedereinfuhrDTO.getWiedereinfuhrLand()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WIEDEREINFUHR, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		}

		final List<ExportsExpdatNamlichkeitsmittelHeaderTabDTO> namlichkeitsmittelDtoList =
				exportsExpdatHeaderDTO.getNamlichkeitsmittelHeaderTabDto();
		if (null != namlichkeitsmittelDtoList && !namlichkeitsmittelDtoList.isEmpty())
		{

			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ART, locale), this.property
							.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TEXTLICHEBESCHREIBUNG, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatNamlichkeitsmittelHeaderTabDTO namlichkeitsmittelDTO : namlichkeitsmittelDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(namlichkeitsmittelDTO.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(namlichkeitsmittelDTO.getNamlichkeitsmitteArt()),
						StringUtils.getTrimValueAfterNullCheck(
								namlichkeitsmittelDTO.getNamlichkeitsmittelTextlicheBeschreibung()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAMLICHKEITSMITTEL, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_3, locale);
		}

		final List<ExportsExpdatErzeugnisHeaderTabDTO> erzeugnisDtoList =
				exportsExpdatHeaderDTO.getErzeugnisHeaderTabDto();
		if (null != erzeugnisDtoList && !erzeugnisDtoList.isEmpty())
		{

			this.contentMap = new LinkedHashMap<>();
			addStringArrayToMapForTabs(ExportsExpdatMessagePdfConstants.KEY_INDEX_1,
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_POSITION, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENNUMMER, locale),
					this.property.getProperty(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_WARENBEZEICHNUNG, locale));
			int indexForTabs = ExportsExpdatMessagePdfConstants.KEY_INDEX_2;
			for (final ExportsExpdatErzeugnisHeaderTabDTO importsContainerDTO : erzeugnisDtoList)
			{
				addStringArrayToMapForTabs(indexForTabs,
						StringUtils.getTrimValueAfterNullCheck(importsContainerDTO.getPosition()),
						StringUtils.getTrimValueAfterNullCheck(importsContainerDTO.getErzeugnisWarennummer()),
						StringUtils.getTrimValueAfterNullCheck(importsContainerDTO.getErzeugnisWarenbezeichnung()));
				indexForTabs++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ERZEUGNIS, true,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_3, locale);
		}
	}

	/**
	 * Adds the header passive veredelung details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09 2021
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderPassiveVeredelungPanelDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getPassiveVeredelungDatumWiedereinfuhr(),
				exportsExpdatHeaderDTO.getPassiveVeredelungStandardaustausch()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PASSIVEVEREDELUNG, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getPassiveVeredelungDatumWiedereinfuhr()))
			{
				addStringArrayToMapForSingleFields(index,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_DATUMDERWIEDEREINFUHR,
						exportsExpdatHeaderDTO.getPassiveVeredelungDatumWiedereinfuhr(), locale);
				index++;
			}
			if ("1".equals(exportsExpdatHeaderDTO.getPassiveVeredelungStandardaustausch()))
			{
				addStringArrayToMapForSingleFields(index,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STANDARDAUSTAUSCH, "1 - ja", locale);
				index++;
			}
			else
			{
				addStringArrayToMapForSingleFields(index,
						ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STANDARDAUSTAUSCH, "0 - nein", locale);
				index++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
	}

	/**
	 * Adds the header verschlussen details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Feb 09 2021
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderVerschlussenPanelDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.needToWrite(exportsExpdatHeaderDTO.getVerschlusseArt(),
				exportsExpdatHeaderDTO.getVerschlusseAnzahl()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANGABENZUDENVERSCHLUSSEN,
					false, ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerschlusseArt()))
			{
				addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ART,
						exportsExpdatHeaderDTO.getVerschlusseArt(), locale);
				index++;
			}
			if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerschlusseAnzahl()))
			{
				addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANZAHL,
						exportsExpdatHeaderDTO.getVerschlusseAnzahl(), locale);
				index++;
			}
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
	}

	/**
	 * Adds the header anzahl positionen details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderAnzahlPositionenPanelDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAnzahlPositionen()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ALLGEMEINEANGABEN, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANZAHLPOSITIONEN,
					exportsExpdatHeaderDTO.getAnzahlPositionen(), locale);
			index++;

			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			contentMap.clear();
		}
	}

	/**
	 * Adds the header vertreter des anmelders address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderVertreterDesAnmeldersAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERTRETERDESANMELDERS, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getTin()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getNiederlassungsNummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getNiederlassungsNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getStrasse()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getStrasse(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getPlz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getPlz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getLand()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getLand(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header vertreter des anmelders ansprechpartner address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderVertreterDesAnmeldersAnsprechpartnerAddressDetails(
			final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSPRECHPARTNER, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechStellung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STELLUNG,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechStellung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SACHBEARBEITER,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getPhone()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFONNUMMER,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getPhone(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechTelefax()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFAXNUMMER,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechTelefax(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechEmail()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMAILADRESSE,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getAnsprechEmail(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getReferenceNum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENCENAME,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getReferenceNum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getTinNeuValue(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVertreterAnmelders().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatHeaderDTO.getVertreterAnmelders().getTinNeuValue(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header subunternehmer address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderSubunternehmerAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SUBUNTERNEHMER, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getSubunternehmer().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getTin()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getSubunternehmer().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getNiederlassungsNummer()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getSubunternehmer().getNiederlassungsNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getName()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatHeaderDTO.getSubunternehmer().getName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getStrasse()))
		{

			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getSubunternehmer().getStrasse(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getOrt()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getSubunternehmer().getOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getPlz()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getSubunternehmer().getPlz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getLand()))
		{

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatHeaderDTO.getSubunternehmer().getLand(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header subunternehmer ansprechpartner address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderSubunternehmerAnsprechpartnerAddressDetails(
			final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSPRECHPARTNER, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechStellung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STELLUNG,
					exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechStellung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SACHBEARBEITER,
					exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getPhone()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFONNUMMER,
					exportsExpdatHeaderDTO.getSubunternehmer().getPhone(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechTelefax()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFAXNUMMER,
					exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechTelefax(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechEmail()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMAILADRESSE,
					exportsExpdatHeaderDTO.getSubunternehmer().getAnsprechEmail(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getReferenceNum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENCENAME,
					exportsExpdatHeaderDTO.getSubunternehmer().getReferenceNum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatHeaderDTO.getSubunternehmer().getTinNeuValue(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getSubunternehmer().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatHeaderDTO.getSubunternehmer().getTinNeuValue(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header ausfuhrer address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderAusfuhrerAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_AUSFUHRER, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);

			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getAusfuhrer().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getTin()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getAusfuhrer().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getNiederlassungsNummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getAusfuhrer().getNiederlassungsNummer(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NAME,
					exportsExpdatHeaderDTO.getAusfuhrer().getName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getStrasse()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STRASSEUNDHAUSNUMMER,
					exportsExpdatHeaderDTO.getAusfuhrer().getStrasse(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getOrt()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ORT,
					exportsExpdatHeaderDTO.getAusfuhrer().getOrt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getPlz()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_PLZ,
					exportsExpdatHeaderDTO.getAusfuhrer().getPlz(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getLand()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_LAND,
					exportsExpdatHeaderDTO.getAusfuhrer().getLand(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header ausfuhrer ansprechpartner address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderAusfuhrerAnsprechpartnerAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSPRECHPARTNER, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechStellung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STELLUNG,
					exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechStellung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SACHBEARBEITER,
					exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getPhone()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFONNUMMER,
					exportsExpdatHeaderDTO.getAusfuhrer().getPhone(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechTelefax()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFAXNUMMER,
					exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechTelefax(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechEmail()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMAILADRESSE,
					exportsExpdatHeaderDTO.getAusfuhrer().getAnsprechEmail(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getReferenceNum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENCENAME,
					exportsExpdatHeaderDTO.getAusfuhrer().getReferenceNum(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TINNEU,
					exportsExpdatHeaderDTO.getAusfuhrer().getTinNeuValue(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getAusfuhrer().getTinNeuValue()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_GULTIGABDATUM,
					exportsExpdatHeaderDTO.getAusfuhrer().getTinNeuValue(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header verfahrensinhaber pv address details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * 
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderVerfahrensinhaberPvAddressDetails(final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO,
			final String locale) throws Exception
	{
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getIdentificationArt()))
		{
			addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_VERFAHRENSINHABER_PV, false,
					ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_IDENTIFIKATIONSART,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getIdentificationArt(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getTin()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TIN,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getTin(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getNiederlassungsNummer()))
		{
			addStringArrayToMapForSingleFields(index,
					ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_NIEDERLASSUNGSNUMMER,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getNiederlassungsNummer(), locale);
			index++;
		}
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

	/**
	 * Adds the header verfahrensinhaber pv ansprechpartner details in PDF
	 *
	 * @author ajeesh.mathew
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate 23 June 2020
	 * @param exportsExpdatHeaderDTO the exports expdat header dto
	 * @param locale the locale
	 * @throws Exception the exception
	 */
	public void addHeaderVerfahrensinhaberPvAnsprechpartnerAddressDetails(
			final ExportsExpdatHeaderDTO exportsExpdatHeaderDTO, final String locale) throws Exception
	{
		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_ANSPRECHPARTNER, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechStellung()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_STELLUNG,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechStellung(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechName()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_SACHBEARBEITER,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechName(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getPhone()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFONNUMMER,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getPhone(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechTelefax()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_TELEFAXNUMMER,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechTelefax(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechEmail()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_EMAILADRESSE,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getAnsprechEmail(), locale);
			index++;
		}
		if (StringUtils.isNotNullOrEmpty(exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getReferenceNum()))
		{
			addStringArrayToMapForSingleFields(index, ExportsExpdatMessagePdfConstants.EXPORT_EXPDAT_REFERENCENAME,
					exportsExpdatHeaderDTO.getVerfahrensinhaberPv().getReferenceNum(), locale);
			index++;
		}

		addSubHeadingAndColumnsValues(ExportsExpdatMessagePdfConstants.EMPTY_STRING, false,
				ExportsExpdatMessagePdfConstants.KEY_INDEX_2, locale);
		contentMap.clear();
	}

}
