/*
 * Copyright 2017 - Till Date BEO Softwares GmbH. All Rights Reserved.
 */
package com.beo.atlas.processor.writer.exports.expdat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.beo.atlas.processor.writer.exports.ExportsCommonFileCreator;
import com.beo.atlas.processor.writer.exports.ExportsXmlFileCreator;
import com.beo.atlas.processor.writer.exports.ExportsXmlTagsConstants;
import com.beo.atlas.rio.exports.dto.address.ExportsAddressDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.ExportsExpdatMessageDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatBeforderungsrouteHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatBesondererHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatErzeugnisHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatHeaderDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatNamlichkeitsmittelHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatVerschlusseZeichenHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatWiedereinfuhrHeaderTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatBeendigungAvuvPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatBeendigungZlPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatContainerPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatPackstuckPositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatPositionDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatUnterlagePositionTabDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatVorpapierPositionTabDTO;
import com.beo.atlas.util.converter.GeneralCodeConverter;
import com.beo.atlas.util.utility.AtlasException;
import com.beo.atlas.util.utility.DateUtils;
import com.beo.atlas.util.utility.StringUtils;

/**
 * The Class ExportsExpdatXmlCreator used for creating xml for EXPDAT message.
 *
 * @author shilpa.balachandran
 * @version 1.0
 * @since Sep 13, 2017
 */
public class ExportsExpdatXmlCreator extends ExportsXmlFileCreator
{

	private static final char SINGLE_SPACE_CHAR = ' ';
	private static final char DOT_CHAR = '.';
	private static final char COMMA_CHAR = ',';
	private static final String SPACE_LITERAL = " ";
	private static final String DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm";
	private static final String LITERAL_M_S = "mS";
	private static final String LITERAL_N_N = "nN";
	private static final String LITERAL_N_K = "nK";
	private static final String LITERAL_N_B = "nB";
	private static final String LITERALN_A = "nA";
	private static final String EMPTY_STRING = "";
	/** The Constant TAGOPEN. */
	private static final String TAGOPEN = "<DEXPDE>";
	/** The Constant TAGCLOSE. */
	private static final String TAGCLOSE = "</DEXPDE>";
	private static final String CURRENT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String CURRENT_TIME_FORMAT = "HH:mm:00";
	private static final String YYYY_MM_DD = "yyyy-MM-dd";
	private static final String DD_MM_YYYY = "dd-MM-yyyy";
	private static final String DATE_EXPECTED_FORMAT = "yyyy-MM-dd'T'HH:mm:00";
	private static final String LITERAL_ONE = "1";
	private static final String LITERAL_COMMA = ",";

	private static final int DIGIT_0 = 0;
	private static final int DIGIT_1 = 1;
	private static final int DIGIT_2 = 2;
	private static final int INTEGER_4 = 4;
	private static final int INT_MINUS_ONE = -1;

	/** The xmlCont. */
	private transient String xmlCont;

	/**
	 * Instantiates a new exports expdat xml creator.
	 */
	public ExportsExpdatXmlCreator()
	{
		super();
	}

	/**
	 * This method invokes createExpdatHeader,after adding all xml data the method invokes createXmlFile() in the
	 * ExportsCommonFileCreator class.
	 * 
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatMessageDto the exp dat message DTO
	 * @return created XML file path
	 */
	public String createMessage(final ExportsExpdatMessageDTO expdatMessageDto)
	{
		if (null == expdatMessageDto || null == expdatMessageDto.getExportsExpdatHeaderDto()
				|| null == expdatMessageDto.getExportsExpdatPositionDto())
		{
			throw new AtlasException();
		}
		final ExportsExpdatHeaderDTO expdatHeaderDto = expdatMessageDto.getExportsExpdatHeaderDto();
		final List<ExportsExpdatPositionDTO> expdatPositionDtoList = expdatMessageDto.getExportsExpdatPositionDto();
		final List<String> filePathList = getValuesForFileName(expdatHeaderDto);
		this.createExpdatHeader(expdatMessageDto);
		this.createExpdatPosition(expdatPositionDtoList,
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getArtderAnmeldungVerfahren()));
		return ExportsCommonFileCreator.getInstance().createXmlFile("EXP-0-", xmlCont, filePathList.get(DIGIT_0),
				filePathList.get(DIGIT_1), filePathList.get(DIGIT_2), expdatMessageDto.getLaufendeNummer());

	}

	/**
	 * This method sets the Eori niedernummer and dienstllen no, nachrichnummer to a list , It uses to take parameters
	 * in filename setting .
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatHeaderDto the expdat header dto
	 * @return list of string
	 */
	private List<String> getValuesForFileName(final ExportsExpdatHeaderDTO expdatHeaderDto)
	{
		final List<String> filePathList = new ArrayList<>();
		final List<String> tinEori = StringUtils.getSplittedList(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getEoriNiederlassungsnummer()), "||");
		String tin = EMPTY_STRING;
		String eorinummer = EMPTY_STRING;
		if (Objects.nonNull(tinEori) && !tinEori.isEmpty())
		{
			final String tinEorivalue = tinEori.get(DIGIT_0);
			final int breakIndex = tinEorivalue.length() - INTEGER_4;
			tin = StringUtils.substringValue(tinEorivalue, DIGIT_0, breakIndex);
			eorinummer = StringUtils.substringValue(tinEorivalue, breakIndex, tinEorivalue.length());
		}
		filePathList.add(StringUtils.getTrimValueAfterNullCheck(tin));
		filePathList.add(StringUtils.getTrimValueAfterNullCheck(eorinummer));
		filePathList.add(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getDienststellennummer()));
		return filePathList;
	}

	/**
	 * This method used to append xml data to stringBuilder object by calling methods in the super class.
	 * 
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 30,2020
	 * 
	 * @param expdatMessageDto expdat message DTO
	 */
	private void createExpdatHeader(final ExportsExpdatMessageDTO expdatMessageDto)
	{
		final ExportsExpdatHeaderDTO expdatHeaderDto = expdatMessageDto.getExportsExpdatHeaderDto();
		final List<String> fieldList = this.getValuesForFileName(expdatHeaderDto);
		this.createHeadTag(TAGOPEN);
		this.createHeaderInterchangeSender(fieldList.get(DIGIT_0), fieldList.get(DIGIT_1));
		this.createHeaderInterchangeRecipient(fieldList.get(DIGIT_2));
		final long messageTimeMilliSeconds = expdatMessageDto.getMessageTimeMilliSeconds();
		this.createHeaderPreparationDateAndTime(
				DateUtils.getFormattedDateTimeForGivenEpochMilliSeconds(messageTimeMilliSeconds, CURRENT_DATE_FORMAT),
				DateUtils.getFormattedDateTimeForGivenEpochMilliSeconds(messageTimeMilliSeconds, CURRENT_TIME_FORMAT));
		this.createHeaderControlReferenceGroupTypeIdentifier(
				StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getInterchangeControlReference()),
				StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getNachrichGruppe()), LITERAL_ONE,
				StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getArtder()),
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getNachrichtennummer()),
				StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getSzenarioNummer()),
				StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getSzenarioHinweis()), LITERAL_ONE);
		this.createHeaderOpenTag();
		final String[] artderanmverfahren = StringUtils.getStringArray(expdatHeaderDto.getArtderAnmeldungVerfahren(),
				ExportsXmlTagsConstants.NUMBER_TWO, SPACE_LITERAL);
		this.createHeaderDeclaration(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getArtderAnmeldungAusfuhr()),
				StringUtils.getTrimValueAfterNullCheck(StringUtils.getArrayValue(artderanmverfahren, DIGIT_0)),
				StringUtils.getTrimValueAfterNullCheck(StringUtils.getArrayValue(artderanmverfahren, DIGIT_1)));
		this.createHeaderMovementReferenceNumber(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getMrn()));
		this.createHeaderMessageVersion(StringUtils.getTrimValueAfterNullCheck(expdatMessageDto.getVersion()));
		this.createHeaderExportCountry(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getAusfuhrland()));
		this.createHeaderDestinationCountry(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBestimmungsLand()));
		this.createHeaderDeclarationDateTime(DateUtils.getFormattedDateTime(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getZeitpunktderAnmeldung()), DD_MM_YYYY_HH_MM,
				DATE_EXPECTED_FORMAT));
		this.createHeaderDecisiveDate(DateUtils.getFormattedDate(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getMassgeblichesDatum()), DD_MM_YYYY,
				YYYY_MM_DD));
		this.createHeaderExitDate(DateUtils.getFormattedDate(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getKopfDatumdesAusgangs()), DD_MM_YYYY,
				YYYY_MM_DD));
		this.createHeaderSpecificCircumstanceIndicator(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBesondereUmstande()));
		this.createHeaderTransportChargesPaymentMethod(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBefrderungskostenZahlungsweise()));
		this.createHeaderPartyConstellation(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeteiligtenKonstellation()));
		this.createHeaderContainerFlag(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getContainer()));
		this.createHeaderTotalGrossMassMeasure(StringUtils.formatOnlyIntegerPart(StringUtils
				.formatFloatNummer(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGesamtRohmasse()))));

		final String registriernummer = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getRegistriernummerFremdsystem()));
		final String vermerk =
				this.removeUnwantedSpaceAfterTrim(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getVermerk()));
		final String kennnummersend = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getKennnummerderSendung()));
		final String bezugsnummer = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBezugsnummer()));
		final String anzahlPositionen = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getAnzahlPositionen());
		this.createHeaderRegAnnoComRefNoLocalRefNoGoodsItemQua(registriernummer, vermerk, kennnummersend, bezugsnummer,
				anzahlPositionen);

		this.createHeaderAuthorizationNumber(StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBin()));

		final String bewilpassivenVeredelung = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getBewilliAnschreibeverfahrenPassivenVeredelung());
		final String passivenVeredelung = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBewilligungPassiveVeredelung()));
		final String zugelassenerAusfuhrer = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBewilligungZugelassenerAusfuhrer()));
		this.createHeaderCustomsAuthorization(bewilpassivenVeredelung, passivenVeredelung, EMPTY_STRING,
				zugelassenerAusfuhrer);
		this.createHeaderInlandTransportMeans(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelImInlandVerkehrszweig()));

		final String abgangArt =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelAbgangArt());
		final String abgangKennzeichen = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelAbgangKennzeichen()));
		final String abgangStaats = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelAbgangStaatszugehorigkeit());
		this.createHeaderDepartureTransportMeans(abgangArt, abgangKennzeichen, abgangStaats);

		final String verkehrszweig =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelderGrenzeVerkehrszweig());
		final String grenzeArt =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelderGrenzeArt());
		final String grenzeKennzeichen = this.removeUnwantedSpaceAfterTrim(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelderGrenzeKennzeichen()));
		final String grenzeStaatszugehorigkeit = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getBeforderungsmittelderGrenzeStaatszugehorigkeit());
		this.createHeaderBorderTransportMeans(verkehrszweig, grenzeArt, grenzeKennzeichen, grenzeStaatszugehorigkeit);

		this.createHeaderCloseTag();
		this.createXmlContentForHeaderTabAndPositionPart(expdatHeaderDto);
	}

	/**
	 * Create xml tag content for header tab and position part.
	 * 
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatHeaderDto the expdat header dto
	 */
	private void createXmlContentForHeaderTabAndPositionPart(final ExportsExpdatHeaderDTO expdatHeaderDto)
	{
		final String ladeortCode = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getWarenortLadeortCode());
		final String ladeortStrass =
				this.removeUnwantedSpaceAfterTrim(expdatHeaderDto.getWarenortLadeortStrassHausnummer());
		final String ladeortplz = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getWarenortLadeortplz());
		final String ladeortOrt = this.removeUnwantedSpaceAfterTrim(expdatHeaderDto.getWarenortLadeortOrt());
		final String ladeortZusatz = this.removeUnwantedSpaceAfterTrim(expdatHeaderDto.getWarenortLadeortZusatz());
		final String ausfuhrzollstelleDienst =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getAusfuhrzollstelleDienststellennummer());
		final String furdieeAMDienst = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getAusfuhrzollstellefurdieeAMDienststellennummer());
		final String vorgeseheneDienst = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getVorgeseheneAusgangszollstelleDienststellennummer());
		final String tatsachlicheDienst = StringUtils
				.getTrimValueAfterNullCheck(expdatHeaderDto.getTatsachlicheAusgangszollstelleDienststellennummer());
		final String vorgangArt = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGeschaftsvorgangArt());
		final String vorgangRechnungspreis =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGeschaftsvorgangRechnungspreis());
		final String vorgangWahrung =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGeschaftsvorgangWahrung());
		this.createHeaderGoodsLoadingPlace(StringUtils.substringValue(ladeortCode, DIGIT_0, INTEGER_4), ladeortStrass,
				ladeortOrt, ladeortplz, ladeortZusatz);
		this.createHeaderExportCustomsOffice(ausfuhrzollstelleDienst);
		this.createHeaderSupplementaryDeclarationCustomsOffice(furdieeAMDienst);
		this.createHeaderIntendedExitCustomsOffice(vorgeseheneDienst);
		this.createHeaderActualExitCustomsOffice(tatsachlicheDienst);
		this.createHeaderTransaction(vorgangArt, vorgangRechnungspreis, vorgangWahrung);
		this.createHeaderSpecialFactForExpDat(expdatHeaderDto.getBesondererHeaderTabDto());
		this.createHeaderItineraryForExpDat(expdatHeaderDto.getBeforderungsrouteHeaderTabDto());
		this.createHeaderSealsForExpDat(expdatHeaderDto);
		final String anfang = DateUtils.getFormattedDateTime(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGestellungVerpackenVerladenAnfang()),
				DD_MM_YYYY_HH_MM, DATE_EXPECTED_FORMAT);
		final String ende = DateUtils.getFormattedDateTime(
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getGestellungVerpackenVerladenEnde()),
				DD_MM_YYYY_HH_MM, DATE_EXPECTED_FORMAT);
		this.createHeaderPresentationPackingLoading(anfang, ende);
		this.createAddressInXml(expdatHeaderDto);
		final String incotermCode =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getLieferbedingungIncotermCode());
		final String textTeil = this.removeUnwantedSpaceAfterTrim(expdatHeaderDto.getLieferbedingungTextTeil());
		final String lieferbedingungOrt = this.removeUnwantedSpaceAfterTrim(expdatHeaderDto.getLieferbedingungOrt());
		final String datumWiedereinfuhr = DateUtils
				.getFormattedDate(expdatHeaderDto.getPassiveVeredelungDatumWiedereinfuhr(), DD_MM_YYYY, YYYY_MM_DD);
		final String standardaustausch =
				StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getPassiveVeredelungStandardaustausch());
		this.createHeaderDeliveryTerms(incotermCode, textTeil, lieferbedingungOrt);
		this.createHeaderOutwardProcessingForExpDat(datumWiedereinfuhr, standardaustausch,
				expdatHeaderDto.getWiedereinfuhrHeaderTabDto(), expdatHeaderDto.getNamlichkeitsmittelHeaderTabDto(),
				expdatHeaderDto.getErzeugnisHeaderTabDto());
	}

	/**
	 * Method Creates the header itinerary tags for expdat xml.
	 * 
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param beforderungsroutedto the beforderungsroutedto
	 */
	private void createHeaderItineraryForExpDat(final ExportsExpdatBeforderungsrouteHeaderTabDTO beforderungsroutedto)
	{
		if (Objects.nonNull(beforderungsroutedto))
		{
			List<String> routeLandList = new ArrayList<>();
			if (Objects.nonNull(beforderungsroutedto.getAusgewahlteLander()))
			{
				routeLandList = beforderungsroutedto.getAusgewahlteLander();
			}
			super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
			super.xmlContent.append(ExportsXmlTagsConstants.COUNTRYOPEN)
					.append(StringUtils.getTrimValueAfterNullCheck(beforderungsroutedto.getBeforderungsVon()))
					.append(ExportsXmlTagsConstants.COUNTRYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			for (final String country : routeLandList)
			{
				final String countryValue = StringUtils.getTrimValueAfterNullCheck(country);
				if (StringUtils.needToWrite(countryValue))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYOPEN)
							.append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.COUNTRYOPEN).append(countryValue)
							.append(ExportsXmlTagsConstants.COUNTRYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
			}
			super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
			super.xmlContent.append(ExportsXmlTagsConstants.COUNTRYOPEN)
					.append(StringUtils.getTrimValueAfterNullCheck(beforderungsroutedto.getBeforderungsBis()))
					.append(ExportsXmlTagsConstants.COUNTRYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			super.xmlContent.append(ExportsXmlTagsConstants.ITINERARYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
	}

	/**
	 * Creates the header special fact for expdat xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param indicatorList the indicator list
	 */
	private void createHeaderSpecialFactForExpDat(final List<ExportsExpdatBesondererHeaderTabDTO> indicatorList)
	{
		if (Objects.nonNull(indicatorList) && !indicatorList.isEmpty())
		{
			for (final ExportsExpdatBesondererHeaderTabDTO indicator : indicatorList)
			{
				if (Objects.nonNull(indicator))
				{
					final String indicatorValue =
							StringUtils.getTrimValueAfterNullCheck(indicator.getBesondererTatbestandKennzeichen());
					if (StringUtils.needToWrite(indicatorValue))
					{
						this.xmlContent.append(ExportsXmlTagsConstants.SPECIALFACTOPEN)
								.append(ExportsXmlTagsConstants.NEXTLINE).append(ExportsXmlTagsConstants.INDICATOROPEN)
								.append(indicatorValue).append(ExportsXmlTagsConstants.INDICATORCLOSE)
								.append(ExportsXmlTagsConstants.NEXTLINE)
								.append(ExportsXmlTagsConstants.SPECIALFACTCLOSE);
						this.addNewLine();

					}
				}
			}
		}
	}

	/**
	 * Creates the header seals tags for expdat xml..
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatHeaderDto the expdat header dto
	 */
	private void createHeaderSealsForExpDat(final ExportsExpdatHeaderDTO expdatHeaderDto)
	{
		final String type = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getVerschlusseArt());
		final String number = StringUtils.getTrimValueAfterNullCheck(expdatHeaderDto.getVerschlusseAnzahl());
		if (StringUtils.needToWrite(type, number))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.SEALSOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
			if (StringUtils.needToWrite(type))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.TYPEOPEN).append(type)
						.append(ExportsXmlTagsConstants.TYPECLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(number))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.NUMBEROPEN).append(number)
						.append(ExportsXmlTagsConstants.NUMBERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			final List<ExportsExpdatVerschlusseZeichenHeaderTabDTO> identityList =
					expdatHeaderDto.getVerschlusseZeichenHeaderTabDto();
			if (Objects.nonNull(identityList) && !identityList.isEmpty())
			{
				for (final ExportsExpdatVerschlusseZeichenHeaderTabDTO identity : identityList)
				{
					this.createSealIdentityTag(identity);
				}
			}
			this.xmlContent.append(ExportsXmlTagsConstants.SEALSCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
	}

	/**
	 * Creates the seal identity tag for expdat xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param identity the identity
	 */
	private void createSealIdentityTag(final ExportsExpdatVerschlusseZeichenHeaderTabDTO identity)
	{
		if (Objects.nonNull(identity))
		{
			final String identityValue = this.removeUnwantedSpaceAfterTrim(identity.getVerschlusseZeichen());
			if (StringUtils.needToWrite(identityValue))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.SEALOPEN).append(ExportsXmlTagsConstants.NEXTLINE)
						.append(ExportsXmlTagsConstants.IDENTITYOPEN)
						.append(GeneralCodeConverter.xmlConverter(identityValue))
						.append(ExportsXmlTagsConstants.IDENTITYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE)
						.append(ExportsXmlTagsConstants.SEALCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
		}
	}

	/**
	 * Method Creates the header outward processing tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param reimportDate the re import date
	 * @param usualReplacement the usual replacement
	 * @param countryList the country list
	 * @param identificationMeansList the identification means list
	 * @param productList the product list
	 */
	private void createHeaderOutwardProcessingForExpDat(final String reimportDate, final String usualReplacement,
			final List<ExportsExpdatWiedereinfuhrHeaderTabDTO> countryList,
			final List<ExportsExpdatNamlichkeitsmittelHeaderTabDTO> identificationMeansList,
			final List<ExportsExpdatErzeugnisHeaderTabDTO> productList)
	{
		final boolean countryListNotNullNotEmpty = Objects.nonNull(countryList) && !countryList.isEmpty();
		final boolean identificationMeansListNotNullNotEmpty =
				Objects.nonNull(identificationMeansList) && !identificationMeansList.isEmpty();
		final boolean productListNotNullNotEmpty = Objects.nonNull(productList) && !productList.isEmpty();

		if (StringUtils.needToWrite(reimportDate, usualReplacement)
				&& (countryListNotNullNotEmpty || identificationMeansListNotNullNotEmpty || productListNotNullNotEmpty))
		{
			super.xmlContent.append(ExportsXmlTagsConstants.OUTWARDPROCESSINGOPEN)
					.append(ExportsXmlTagsConstants.NEXTLINE);
			if (StringUtils.needToWrite(reimportDate))
			{
				super.xmlContent.append(ExportsXmlTagsConstants.REIMPORTDATEOPEN).append(reimportDate)
						.append(ExportsXmlTagsConstants.REIMPORTDATECLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(usualReplacement))
			{
				super.xmlContent.append(ExportsXmlTagsConstants.USUALREPLACEMENTOPEN).append(usualReplacement)
						.append(ExportsXmlTagsConstants.USUALREPLACEMENTCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			this.createHeaderReimportTag(countryList);
			this.createHeaderOutwardProcessingForExpDatSub(identificationMeansList);
			this.createHeaderOutwardProcessingForExpDatSub1(productList);
			super.xmlContent.append(ExportsXmlTagsConstants.OUTWARDPROCESSINGCLOSE)
					.append(ExportsXmlTagsConstants.NEXTLINE);
		}

	}

	/**
	 * Creates the header reimport tags for EXP DAT xml..
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param countryList the country list
	 */
	private void createHeaderReimportTag(final List<ExportsExpdatWiedereinfuhrHeaderTabDTO> countryList)
	{
		if (Objects.nonNull(countryList) && !countryList.isEmpty())
		{
			for (final ExportsExpdatWiedereinfuhrHeaderTabDTO country : countryList)
			{
				final String countryValue = StringUtils.getTrimValueAfterNullCheck(country.getWiedereinfuhrLand());
				if (StringUtils.needToWrite(countryValue))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.REIMPORTOPEN)
							.append(ExportsXmlTagsConstants.NEXTLINE).append(ExportsXmlTagsConstants.COUNTRYOPEN)
							.append(countryValue).append(ExportsXmlTagsConstants.COUNTRYCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE).append(ExportsXmlTagsConstants.REIMPORTCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
			}
		}
	}

	/**
	 * Method Creates the header outward processing tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param productList the product list
	 */
	private void createHeaderOutwardProcessingForExpDatSub1(final List<ExportsExpdatErzeugnisHeaderTabDTO> productList)
	{
		if (Objects.nonNull(productList) && !productList.isEmpty())
		{
			for (final ExportsExpdatErzeugnisHeaderTabDTO product : productList)
			{
				if ((Objects.nonNull(product)))
				{
					final String erzeugnisWarennummer =
							StringUtils.getTrimValueAfterNullCheck(product.getErzeugnisWarennummer());
					final String erzeugnisWarennummerDescription =
							this.removeUnwantedSpaceAfterTrim(product.getErzeugnisWarenbezeichnung());
					super.xmlContent.append(ExportsXmlTagsConstants.PRODUCTOPEN)
							.append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.COMMODITYCODEOPEN).append(erzeugnisWarennummer)
							.append(ExportsXmlTagsConstants.COMMODITYCODECLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.GOODSDESCRIPTIONOPEN)
							.append(GeneralCodeConverter.xmlConverter(erzeugnisWarennummerDescription))
							.append(ExportsXmlTagsConstants.GOODSDESCRIPTIONCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.PRODUCTCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
			}
		}
	}

	/**
	 * Method Creates the header outward processing tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param identificationMeansList the identification means list
	 */
	private void createHeaderOutwardProcessingForExpDatSub(
			final List<ExportsExpdatNamlichkeitsmittelHeaderTabDTO> identificationMeansList)
	{
		if (Objects.nonNull(identificationMeansList) && !identificationMeansList.isEmpty())
		{
			for (final ExportsExpdatNamlichkeitsmittelHeaderTabDTO identificationMeans : identificationMeansList)
			{
				if (Objects.nonNull(identificationMeans))
				{
					final String namlichkeitsmitteArt =
							StringUtils.getTrimValueAfterNullCheck(identificationMeans.getNamlichkeitsmitteArt());
					final String namlichkeitsmitteDescription = this.removeUnwantedSpaceAfterTrim(
							identificationMeans.getNamlichkeitsmittelTextlicheBeschreibung());
					super.xmlContent.append(ExportsXmlTagsConstants.IDENTIFICATIONMEANSOPEN)
							.append(ExportsXmlTagsConstants.NEXTLINE);
					super.xmlContent.append(ExportsXmlTagsConstants.TYPEOPEN).append(namlichkeitsmitteArt)
							.append(ExportsXmlTagsConstants.TYPECLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
					if (StringUtils.needToWrite(namlichkeitsmitteDescription))
					{
						super.xmlContent.append(ExportsXmlTagsConstants.DESCRIPTIONOPEN)
								.append(GeneralCodeConverter.xmlConverter(namlichkeitsmitteDescription))
								.append(ExportsXmlTagsConstants.DESCRIPTIONCLOSE)
								.append(ExportsXmlTagsConstants.NEXTLINE);
					}
					super.xmlContent.append(ExportsXmlTagsConstants.IDENTIFICATIONMEANSCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
			}
		}
	}

	/**
	 * This method for creating address tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy rajeev.k@beo.in
	 * @modifiedDate Jan 16,2019
	 * 
	 * @param expdatHeaderDto the expdat header dto
	 */
	private void createAddressInXml(final ExportsExpdatHeaderDTO expdatHeaderDto)
	{
		this.createAddress(expdatHeaderDto.getAusfuhrer(), ExportsXmlTagsConstants.EXPORTEROPEN,
				ExportsXmlTagsConstants.EXPORTERCLOSE);
		this.createAddress(expdatHeaderDto.getAnmelder(), ExportsXmlTagsConstants.DECLARANTOPEN,
				ExportsXmlTagsConstants.DECLARANTCLOSE);
		this.createAddress(expdatHeaderDto.getVertreterAnmelders(), ExportsXmlTagsConstants.REPRESENTATIVEOPEN,
				ExportsXmlTagsConstants.REPRESENTATIVECLOSE);
		this.createAddress(expdatHeaderDto.getSubunternehmer(), ExportsXmlTagsConstants.CONTRACTOROPEN,
				ExportsXmlTagsConstants.CONTRACTORCLOSE);
		this.createAddress(expdatHeaderDto.getVerfahrensinhaberPv(), ExportsXmlTagsConstants.OUTWARDPROCESSINGOWNEROPEN,
				ExportsXmlTagsConstants.OUTWARDPROCESSINGOWNERCLOSE);
		final ExportsAddressDTO empfanger = expdatHeaderDto.getEmpfanger();
		clearContactPersonValues(empfanger);
		this.createAddress(empfanger, ExportsXmlTagsConstants.CONSIGNEEOPEN, ExportsXmlTagsConstants.CONSIGNEECLOSE);
	}

	/**
	 * Method to Clear contact person values to avoid writing contact person in out xml.
	 * 
	 * @author rajeev.k@beo.in
	 * @modifiedBy rajeev.k@beo.in
	 * @modifiedDate Jan 16,2019
	 * 
	 * @param exportsAddressDTO the exports address DTO
	 */
	private void clearContactPersonValues(final ExportsAddressDTO exportsAddressDTO)
	{
		exportsAddressDTO.setAnsprechStellung(EMPTY_STRING);
		exportsAddressDTO.setAnsprechName(EMPTY_STRING);
		exportsAddressDTO.setPhone(EMPTY_STRING);
		exportsAddressDTO.setAnsprechTelefax(EMPTY_STRING);
		exportsAddressDTO.setAnsprechEmail(EMPTY_STRING);
	}

	/**
	 * This method used to append data of position object in EXP DAT xml..
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPositionDtoList the expdat position dto list
	 * @param artderAnmeldungVerfahren the artder anmeldung verfahren
	 */
	private void createExpdatPosition(final List<ExportsExpdatPositionDTO> expdatPositionDtoList,
			final String artderAnmeldungVerfahren)
	{
		if (!expdatPositionDtoList.isEmpty())
		{
			for (final ExportsExpdatPositionDTO expdatPosDto : expdatPositionDtoList)
			{
				if (Objects.nonNull(expdatPosDto))
				{
					this.createPositionOpenTag();
					this.createPositionSequenceNumber(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWarePositionsnummer()));
					this.createPositionGoodsDescription(
							this.removeUnwantedSpaceAfterTrim(expdatPosDto.getWareWarenbezeichnung()));
					this.createPositionCommercialReferenceNumber(
							this.removeUnwantedSpaceAfterTrim(expdatPosDto.getWareKennnummerSendung()));
					this.createPositionRegistrationNumber(
							this.removeUnwantedSpaceAfterTrim(expdatPosDto.getWareRegistriernummerFremdsystem()));
					this.createPositionAnnotation(this.removeUnwantedSpaceAfterTrim(expdatPosDto.getWareVermerk()));
					this.createPositionOriginFederalState(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareUrsprungsbundesland()));
					this.createPositionNetMass(StringUtils.formatOnlyIntegerPart(StringUtils.formatFloatNummer(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareEigenmasse()))));
					this.createPositionGrossMass(StringUtils.formatOnlyIntegerPart(StringUtils.formatFloatNummer(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareRohmasse()))));
					this.createPositionDangerousGoodsCode(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareGefahrgutnummerUndg()));
					this.createPositionTransportChargesPaymentMethod(StringUtils
							.getTrimValueAfterNullCheck(expdatPosDto.getWareBeforderungskostenZahlungsweise()));

					final String kn8 = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareWarennummerKN8());
					final String taric = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareWarennummerTaric());
					final String ersterZusatz =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareWarennummerErsterZusatz());
					final String zweiterZusatz =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareWarennummerZweiterZusatz());
					final String nationale =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getWareWarennummerNationaleAngaben());
					this.createPositionCommodityCode(kn8, taric, ersterZusatz, zweiterZusatz, nationale);

					final String angemeldetes =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getVerfahrenAngemeldetes());
					final String vorangegangenes =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getVerfahrenVorangegangenes());
					final String weiteres = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getVerfahrenWeiteres());
					final String verfAusfuhrerstattung =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getVerfahrenAusfuhrerstattung());
					this.createPositionProcedure(angemeldetes, vorangegangenes, weiteres, verfAusfuhrerstattung);

					this.createPositionExportRefund(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getAusfuhrerstattungMenge()));

					final String aussenMenge = StringUtils.removeZero(
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getAussenhandelsstatistikMenge()));
					final String aussenWert =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getAussenhandelsstatistikWert());
					this.createPositionForeignTradeStatistics(aussenMenge, aussenWert);

					final ExportsAddressDTO empfanger = expdatPosDto.getEmpfanger();
					this.clearContactPersonValues(empfanger);
					this.createAddress(empfanger, ExportsXmlTagsConstants.CONSIGNEEOPEN,
							ExportsXmlTagsConstants.CONSIGNEECLOSE);

					this.createPackstuckTab(expdatPosDto);
					this.createContainerTab(expdatPosDto);
					this.createUnterlageTab(expdatPosDto);
					this.createVorpapierTab(expdatPosDto);
					final String incoterm =
							StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getLieferbedingungIncotermCode());
					final String text = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getLieferbedingungText());
					final String ort = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getLieferbedingungOrt());
					final String gesart = StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getGeschaftsvorgangArt());
					this.createPositionDeliveryTerms(incoterm, text, ort);
					this.createPositionTransaction(gesart);
					this.createPositionCustomsWarehouseForExpDat(expdatPosDto);
					this.createAvuvTabDetails(artderAnmeldungVerfahren, expdatPosDto, vorangegangenes);
					this.createPositionCloseTag();
				}
			}
			this.createHeadTagClose(TAGCLOSE);
		}
		xmlCont = this.getXmlContent();
	}

	/**
	 * Method Creates the position customs warehouse tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPosDto the expdat pos dto
	 */
	private void createPositionCustomsWarehouseForExpDat(final ExportsExpdatPositionDTO expdatPosDto)
	{
		final String goodsItemQuantity =
				StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getBeendigungZLAnzahlPositionen());
		final String warehouseOwner = GeneralCodeConverter
				.xmlConverter(this.removeUnwantedSpaceAfterTrim(expdatPosDto.getBeendigungZLBewilligungsnummer()));
		final String localReferenceNumber = GeneralCodeConverter
				.xmlConverter(this.removeUnwantedSpaceAfterTrim(expdatPosDto.getBeendigungZLBezugsnummer()));
		final List<ExportsExpdatBeendigungZlPositionTabDTO> goodsItemList =
				expdatPosDto.getBeendigungZlPositionTabDto();
		if (Objects.nonNull(goodsItemList)
				&& StringUtils.needToWrite(goodsItemQuantity, warehouseOwner, localReferenceNumber)
				&& !goodsItemList.isEmpty())
		{
			this.xmlContent.append(ExportsXmlTagsConstants.CUSTOMSWAREHOUSEOPEN)
					.append(ExportsXmlTagsConstants.NEXTLINE);
			if (StringUtils.needToWrite(goodsItemQuantity))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMQUANTITYOPEN).append(goodsItemQuantity)
						.append(ExportsXmlTagsConstants.GOODSITEMQUANTITYCLOSE)
						.append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(warehouseOwner))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.CUSTOMSAUTHORIZATIONOPEN)
						.append(ExportsXmlTagsConstants.NEXTLINE).append(ExportsXmlTagsConstants.WAREHOUSEOWNEROPEN)
						.append(warehouseOwner).append(ExportsXmlTagsConstants.WAREHOUSEOWNERCLOSE)
						.append(ExportsXmlTagsConstants.NEXTLINE)
						.append(ExportsXmlTagsConstants.CUSTOMSAUTHORIZATIONCLOSE)
						.append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(localReferenceNumber))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.LOCALREFERENCENUMBEROPEN).append(localReferenceNumber)
						.append(ExportsXmlTagsConstants.LOCALREFERENCENUMBERCLOSE)
						.append(ExportsXmlTagsConstants.NEXTLINE);
			}
			for (final ExportsExpdatBeendigungZlPositionTabDTO goodsItem : goodsItemList)
			{
				final String satznummer = StringUtils.getTrimValueAfterNullCheck(goodsItem.getPositionZLSatznummer());
				this.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
				if (StringUtils.needToWrite(satznummer))
				{
					this.xmlContent.append(ExportsXmlTagsConstants.SEQUENCENUMBEROPEN).append(satznummer)
							.append(ExportsXmlTagsConstants.SEQUENCENUMBERCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
				this.createPositionCustomsWarehouseForExpDatpartTwo(goodsItem);
				this.createPositionCustomsWarehouseForExpDatpartThree(goodsItem);
				this.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			this.xmlContent.append(ExportsXmlTagsConstants.CUSTOMSWAREHOUSECLOSE)
					.append(ExportsXmlTagsConstants.NEXTLINE);
		}
	}

	/**
	 * Method to solve the complexity of method createPositionCustomsWarehouseForExpDat
	 * 
	 * @author ashlin.jerson
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param goodsItem the ExportsExpdatBeendigungZlPositionTabDTO
	 */
	private void createPositionCustomsWarehouseForExpDatpartTwo(final ExportsExpdatBeendigungZlPositionTabDTO goodsItem)
	{
		final String positionnummer = StringUtils.getTrimValueAfterNullCheck(goodsItem.getPositionZLPositionsnummer());
		if (StringUtils.needToWrite(positionnummer))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.REFERENCEDSEQUENCENUMBEROPEN).append(positionnummer)
					.append(ExportsXmlTagsConstants.REFERENCEDSEQUENCENUMBERCLOSE)
					.append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String registernummer = this.removeUnwantedSpaceAfterTrim(goodsItem.getPositionZLRegistriernummer());
		if (StringUtils.needToWrite(registernummer))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.REGISTRATIONNUMBEROPEN)
					.append(GeneralCodeConverter.xmlConverter(registernummer))
					.append(ExportsXmlTagsConstants.REGISTRATIONNUMBERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String warennummer = StringUtils.getTrimValueAfterNullCheck(goodsItem.getPositionZLWarennummer());
		if (StringUtils.needToWrite(warennummer))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.COMMODITYCODEOPEN).append(warennummer)
					.append(ExportsXmlTagsConstants.COMMODITYCODECLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String kennzeichenZuganInAtlas =
				StringUtils.getTrimValueAfterNullCheck(goodsItem.getPositionZLKennzeichenZuganInAtlas());
		if (StringUtils.needToWrite(kennzeichenZuganInAtlas))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.ACCESSVIAATLASFLAGOPEN).append(kennzeichenZuganInAtlas)
					.append(ExportsXmlTagsConstants.ACCESSVIAATLASFLAGCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String ublicheBehandlung =
				StringUtils.getTrimValueAfterNullCheck(goodsItem.getPositionZLKennzeichenUblicheBehandlung());
		if (StringUtils.needToWrite(ublicheBehandlung))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.USUALPROCESSINGFLAGOPEN).append(ublicheBehandlung)
					.append(ExportsXmlTagsConstants.USUALPROCESSINGFLAGCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String zusats = this.removeUnwantedSpaceAfterTrim(goodsItem.getPositioZlZusatz());
		if (StringUtils.needToWrite(zusats))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.COMPLEMENTOPEN)
					.append(GeneralCodeConverter.xmlConverter(zusats)).append(ExportsXmlTagsConstants.COMPLEMENTCLOSE)
					.append(ExportsXmlTagsConstants.NEXTLINE);
		}
	}

	/**
	 * Method to solve the complexity of method createPositionCustomsWarehouseForExpDat
	 * 
	 * @author ashlin.jerson
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param goodsItem the ExportsExpdatBeendigungZlPositionTabDTO
	 */
	private void
			createPositionCustomsWarehouseForExpDatpartThree(final ExportsExpdatBeendigungZlPositionTabDTO goodsItem)
	{
		final String abgangsmengequalifikatort =
				StringUtils.getTrimValueAfterNullCheck(goodsItem.getAbgangsmengeQualifikator());
		final String abgangsmengeMasseinheit =
				StringUtils.getTrimValueAfterNullCheck(goodsItem.getAbgangsmengeMasseinheit());
		final String quantity =
				StringUtils.getTrimValueAfterNullCheck(goodsItem.getAbgangsmengesWert()).replace(COMMA_CHAR, DOT_CHAR);
		if (StringUtils.needToWrite(abgangsmengequalifikatort, abgangsmengeMasseinheit, quantity))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.DEBITAMOUNTOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
			if (StringUtils.needToWrite(abgangsmengequalifikatort))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.QUALIFIEROPEN).append(abgangsmengequalifikatort)
						.append(ExportsXmlTagsConstants.QUALIFIERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}

			if (StringUtils.needToWrite(abgangsmengeMasseinheit))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.MEASUREMENTUNITOPEN)
						.append(this.getValueBeforeSpace(abgangsmengeMasseinheit))
						.append(ExportsXmlTagsConstants.MEASUREMENTUNITCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}

			if (StringUtils.needToWrite(quantity))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.QUANTITYOPEN)
						.append(GeneralCodeConverter.xmlConverter(quantity))
						.append(ExportsXmlTagsConstants.QUANTITYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			this.xmlContent.append(ExportsXmlTagsConstants.DEBITAMOUNTCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		final String debitamount = StringUtils.getTrimValueAfterNullCheck(goodsItem.getHandelsmengesQualifikator());
		final String measurement = StringUtils.getTrimValueAfterNullCheck(goodsItem.getHandelsmengeMasseinheit());
		final String handelsmengeWertQuantity = StringUtils.getTrimValueAfterNullCheck(goodsItem.getHandelsmengeWert());
		if (StringUtils.needToWrite(debitamount, measurement, handelsmengeWertQuantity))
		{
			this.xmlContent.append(ExportsXmlTagsConstants.COMMERCIALAMOUNTOPEN)
					.append(ExportsXmlTagsConstants.NEXTLINE);
			if (StringUtils.needToWrite(debitamount))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.QUALIFIEROPEN)
						.append(StringUtils.replaceCharacter(GeneralCodeConverter.xmlConverter(debitamount), COMMA_CHAR,
								DOT_CHAR))
						.append(ExportsXmlTagsConstants.QUALIFIERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(measurement))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.MEASUREMENTUNITOPEN)
						.append(this.getValueBeforeSpace(measurement))
						.append(ExportsXmlTagsConstants.MEASUREMENTUNITCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			if (StringUtils.needToWrite(handelsmengeWertQuantity))
			{
				this.xmlContent.append(ExportsXmlTagsConstants.QUANTITYOPEN)
						.append(StringUtils.replaceCharacter(
								GeneralCodeConverter.xmlConverter(handelsmengeWertQuantity), COMMA_CHAR, DOT_CHAR))
						.append(ExportsXmlTagsConstants.QUANTITYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
			}
			this.xmlContent.append(ExportsXmlTagsConstants.COMMERCIALAMOUNTCLOSE)
					.append(ExportsXmlTagsConstants.NEXTLINE);
		}
	}

	/**
	 * Gets the value before space.
	 *
	 * @author laya.venugopal
	 * @modifiedBy
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param value the value
	 * @return the value before space
	 */
	private String getValueBeforeSpace(final String value)
	{
		String result = value;
		final int indexOfSpace = value.indexOf(SINGLE_SPACE_CHAR);
		if (indexOfSpace != INT_MINUS_ONE)
		{
			result = StringUtils.substringValue(value, DIGIT_0, indexOfSpace);
		}
		return result;
	}

	/**
	 * This method creates AVUV tab details tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param artderAnmeldungVerfahren the artder anmeldung verfahren
	 * @param expdatPosDto the expdat pos dto
	 * @param vorangegangenes the vorangegangenes
	 */
	private void createAvuvTabDetails(final String artderAnmeldungVerfahren,
			final ExportsExpdatPositionDTO expdatPosDto, final String vorangegangenes)
	{
		if (this.createAvuvTabCondition(artderAnmeldungVerfahren, vorangegangenes))
		{
			final String avuvAnzhal =
					StringUtils.getTrimValueAfterNullCheck(expdatPosDto.getBeendigungAvuvAnzahlPositionen());
			final String avuvBewill =
					this.removeUnwantedSpaceAfterTrim(expdatPosDto.getBeendigungAvuvBewilligungsnummer());
			final List<ExportsExpdatBeendigungAvuvPositionTabDTO> beendigungavuvPositionTabDtoList =
					expdatPosDto.getBeendigungavuvPositionTabDto();
			if (Objects.nonNull(beendigungavuvPositionTabDtoList) && StringUtils.needToWrite(avuvAnzhal, avuvBewill)
					&& !beendigungavuvPositionTabDtoList.isEmpty() && !"-1".equals(avuvAnzhal))
			{
				this.createPositionInwardProcessingTransformationForExpDat(avuvAnzhal, avuvBewill,
						beendigungavuvPositionTabDtoList);
			}
		}
	}

	/**
	 * Creates the avuv tab condition.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param artderAnmeldungVerfahren the artder anmeldung verfahren
	 * @param vorangegangenes the vorangegangenes
	 * @return true, if successful
	 */
	private boolean createAvuvTabCondition(final String artderAnmeldungVerfahren, final String vorangegangenes)
	{
		final boolean isOtherCondition =
				!artderAnmeldungVerfahren.startsWith(LITERAL_N_B) && !artderAnmeldungVerfahren.startsWith(LITERAL_N_K)
						&& !artderAnmeldungVerfahren.startsWith(LITERAL_N_N)
						&& !artderAnmeldungVerfahren.startsWith(LITERAL_M_S);
		return Pattern.compile("02|41|51|91").matcher(vorangegangenes).matches() && isOtherCondition
				&& !artderAnmeldungVerfahren.startsWith(LITERALN_A);
	}

	/**
	 * Creates the position inward processing transformation tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param goodsItemQuantity the goods item quantity
	 * @param processingOwner the processing owner
	 * @param goodsItem the goods item
	 */
	private void createPositionInwardProcessingTransformationForExpDat(final String goodsItemQuantity,
			final String processingOwner, final List<ExportsExpdatBeendigungAvuvPositionTabDTO> goodsItem)
	{

		super.xmlContent.append(ExportsXmlTagsConstants.INWARDPROCESSING_TRANSFORMATIONOPEN)
				.append(ExportsXmlTagsConstants.NEXTLINE);
		if (StringUtils.needToWrite(goodsItemQuantity))
		{
			super.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMQUANTITYOPEN).append(goodsItemQuantity)
					.append(ExportsXmlTagsConstants.GOODSITEMQUANTITYCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		if (StringUtils.needToWrite(processingOwner))
		{
			super.xmlContent.append(ExportsXmlTagsConstants.CUSTOMSAUTHORIZATIONOPEN)
					.append(ExportsXmlTagsConstants.NEXTLINE).append(ExportsXmlTagsConstants.PROCESSINGOWNEROPEN)
					.append(GeneralCodeConverter.xmlConverter(processingOwner))
					.append(ExportsXmlTagsConstants.PROCESSINGOWNERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE)
					.append(ExportsXmlTagsConstants.CUSTOMSAUTHORIZATIONCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		for (final ExportsExpdatBeendigungAvuvPositionTabDTO obj : goodsItem)
		{
			super.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMOPEN).append(ExportsXmlTagsConstants.NEXTLINE);
			final String satznummer = StringUtils.getTrimValueAfterNullCheck(obj.getPositionAvuvSatznummer());
			if (StringUtils.needToWrite(satznummer))
			{
				super.xmlContent.append(ExportsXmlTagsConstants.SEQUENCENUMBEROPEN).append(satznummer)
						.append(ExportsXmlTagsConstants.SEQUENCENUMBERCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);

				final String positionnummer =
						StringUtils.getTrimValueAfterNullCheck(obj.getPositionAvuvPositionsnummer());
				if (StringUtils.needToWrite(positionnummer))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.REFERENCEDSEQUENCENUMBEROPEN).append(positionnummer)
							.append(ExportsXmlTagsConstants.REFERENCEDSEQUENCENUMBERCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
				final String registernummer = this.removeUnwantedSpaceAfterTrim(obj.getPositionAvuvRegistriernummer());
				if (StringUtils.needToWrite(registernummer))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.REGISTRATIONNUMBEROPEN)
							.append(GeneralCodeConverter.xmlConverter(registernummer))
							.append(ExportsXmlTagsConstants.REGISTRATIONNUMBERCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
				final String kennzeichenZugangAtlas =
						StringUtils.getTrimValueAfterNullCheck(obj.getPositionAvuvKennzeichenZugangAtlas());
				if (StringUtils.needToWrite(kennzeichenZugangAtlas))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.ACCESSVIAATLASFLAGOPEN)
							.append(kennzeichenZugangAtlas).append(ExportsXmlTagsConstants.ACCESSVIAATLASFLAGCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
				final String warenbezogeneAngaben =
						this.removeUnwantedSpaceAfterTrim(obj.getPositionAvuvWarenbezogeneAngaben());
				if (StringUtils.needToWrite(warenbezogeneAngaben))
				{
					super.xmlContent.append(ExportsXmlTagsConstants.GOODSRELATEDINFORMATIONOPEN)
							.append(GeneralCodeConverter.xmlConverter(warenbezogeneAngaben))
							.append(ExportsXmlTagsConstants.GOODSRELATEDINFORMATIONCLOSE)
							.append(ExportsXmlTagsConstants.NEXTLINE);
				}
			}
			super.xmlContent.append(ExportsXmlTagsConstants.GOODSITEMCLOSE).append(ExportsXmlTagsConstants.NEXTLINE);
		}
		this.xmlContent.append(ExportsXmlTagsConstants.INWARDPROCESSING_TRANSFORMATIONCLOSE)
				.append(ExportsXmlTagsConstants.NEXTLINE);
	}

	/**
	 * This method creates Container Tab tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPosDto the expdat pos dto
	 */
	private void createContainerTab(final ExportsExpdatPositionDTO expdatPosDto)
	{
		final List<ExportsExpdatContainerPositionTabDTO> container = expdatPosDto.getContainerPositionTabDto();
		if (Objects.nonNull(container) && !container.isEmpty())
		{
			for (final ExportsExpdatContainerPositionTabDTO containerDto : container)
			{
				if (Objects.nonNull(containerDto))
				{
					this.createPositionContainer(this.removeUnwantedSpaceAfterTrim(containerDto.getContainerNumber()));
				}
			}
		}
	}

	/**
	 * This method creates Packstuck Tab tags for EXP DAT xml.
	 * 
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPosDto the expdat pos dto
	 */
	private void createPackstuckTab(final ExportsExpdatPositionDTO expdatPosDto)
	{
		final List<ExportsExpdatPackstuckPositionTabDTO> packstuck = expdatPosDto.getPackstuckPositionTabDto();
		if (Objects.nonNull(packstuck) && !packstuck.isEmpty())
		{
			for (final ExportsExpdatPackstuckPositionTabDTO packstuckDto : packstuck)
			{
				if (Objects.nonNull(packstuckDto))
				{
					final String anzahl = StringUtils.getTrimValueAfterNullCheck(packstuckDto.getPackstuckAnzahl());
					final String nummer = StringUtils.getTrimValueAfterNullCheck(packstuckDto.getPackstuckNummer());
					final String verpackungsart =
							StringUtils.getTrimValueAfterNullCheck(packstuckDto.getPackstuckVerpackungsart());
					final String zeichenNummern =
							this.removeUnwantedSpaceAfterTrim(packstuckDto.getPackstuckZeichenNummern());
					final String hauptpack =
							StringUtils.getTrimValueAfterNullCheck(packstuckDto.getVerpacktInHauptpackPositionsNr());
					this.createPositionPackage(anzahl, nummer, verpackungsart, zeichenNummern, hauptpack);
				}
			}
		}
	}

	/**
	 * This method creates Unterlage Tab fields values in xml content.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPosDto the expdat pos dto
	 */
	private void createUnterlageTab(final ExportsExpdatPositionDTO expdatPosDto)
	{
		final List<ExportsExpdatUnterlagePositionTabDTO> unterlage = expdatPosDto.getUnterlagePositionTabDto();
		if (Objects.nonNull(unterlage) && !unterlage.isEmpty())
		{
			for (final ExportsExpdatUnterlagePositionTabDTO unterlageDto : unterlage)
			{
				if (Objects.nonNull(unterlageDto))
				{
					final String qualifier =
							StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageQualifikator());
					final String typ = StringUtils.substringValue(
							StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageType()), DIGIT_0,
							INTEGER_4);
					final String referenz = this.removeUnwantedSpaceAfterTrim(unterlageDto.getUnterlageReferenz());
					final String zusats = this.removeUnwantedSpaceAfterTrim(unterlageDto.getUnterlageZusatz());
					final String detail = this.removeUnwantedSpaceAfterTrim(unterlageDto.getUnterlageDetail());
					final String dausstellung = DateUtils
							.getFormattedDate(unterlageDto.getUnterlageDatumderAusstellung(), DD_MM_YYYY, YYYY_MM_DD);
					final String datumGultig = DateUtils
							.getFormattedDate(unterlageDto.getUnterlageDatumGultigkeitsendes(), DD_MM_YYYY, YYYY_MM_DD);
					final String wert = StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageWert());
					final String masseinheit =
							StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageMasseinheit());
					String abschmenge =
							StringUtils.getTrimValueAfterNullCheck(unterlageDto.getUnterlageAbschreibungsmenge());
					if (abschmenge.contains(LITERAL_COMMA))
					{
						abschmenge = StringUtils.replaceCharacter(StringUtils.getTrimValueAfterNullCheck(abschmenge),
								COMMA_CHAR, DOT_CHAR);
					}
					this.createPositionDocument(qualifier, typ, referenz, zusats, detail, dausstellung, datumGultig,
							wert, masseinheit, abschmenge);
				}
			}
		}
	}

	/**
	 * This method creates Vorpapier Tab tags for EXP DAT xml.
	 *
	 * @author shilpa.balachandran
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param expdatPosDto the expdat pos dto
	 */
	private void createVorpapierTab(final ExportsExpdatPositionDTO expdatPosDto)
	{
		final List<ExportsExpdatVorpapierPositionTabDTO> vorpapier = expdatPosDto.getVorpapierPositionTabDto();
		if (Objects.nonNull(vorpapier) && !vorpapier.isEmpty())
		{
			for (final ExportsExpdatVorpapierPositionTabDTO vorpapierDto : vorpapier)
			{
				if (Objects.nonNull(vorpapierDto))
				{
					this.createPositionPreviousAdminstrativeReference(
							StringUtils.getTrimValueAfterNullCheck(vorpapierDto.getVorpapierType()),
							this.removeUnwantedSpaceAfterTrim(vorpapierDto.getVorpapierReferenz()),
							this.removeUnwantedSpaceAfterTrim(vorpapierDto.getVorpapierZusatz()));
				}
			}
		}
	}

	/**
	 * Removes the unwanted space after trimming the value.
	 *
	 * @author laya.venugopal@beo.in
	 * @modifiedBy
	 * @modifiedDate Nov 25,2020
	 * 
	 * @param value the value
	 * @return the string
	 */
	private String removeUnwantedSpaceAfterTrim(final String value)
	{
		return StringUtils.removeUnwatedSpaceFromString(StringUtils.getTrimValueAfterNullCheck(value), DIGIT_1);
	}

}
