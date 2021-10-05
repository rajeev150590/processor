/*
 * Copyright 2018 - Till Date BEO Softwares GmbH. All Rights Reserved.
 */
package com.beo.atlas.processor.writer.exports.expdat;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJBException;
import javax.persistence.PersistenceException;

import com.beo.atlas.businessrules.exports.messages.expdat.ExportsExpDatMessageRuleConstants;
import com.beo.atlas.processor.external.customs.german.sending.SendMessage;
import com.beo.atlas.processor.writer.common.messages.MessagesUniqueValuesFinder;
import com.beo.atlas.processor.writer.exports.ExportsCommonFileCreator;
import com.beo.atlas.processor.writer.exports.ExportsMessageProcessHandlerHelper;
import com.beo.atlas.rio.common.JNDIConstants;
import com.beo.atlas.rio.common.RemoteServiceLocator;
import com.beo.atlas.rio.common.dto.MessageIdentifierObject;
import com.beo.atlas.rio.exports.dto.ExportsLogDTO;
import com.beo.atlas.rio.exports.dto.ExportsStatusDTO;
import com.beo.atlas.rio.exports.dto.address.ExportsAddressDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.ExportsExpdatMessageDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.header.ExportsExpdatHeaderDTO;
import com.beo.atlas.rio.exports.dto.messages.expdat.position.ExportsExpdatPositionDTO;
import com.beo.atlas.rio.exports.session.remote.ExportsLogFacadeRemote;
import com.beo.atlas.rio.exports.session.remote.ExportsStatusFacadeRemote;
import com.beo.atlas.rio.exports.session.remote.ExpowinAtlasFacadeRemote;
import com.beo.atlas.rio.exports.session.remote.expdat.ExportsExpDatFacadeRemote;
import com.beo.atlas.util.logger.AtlasLogManager;
import com.beo.atlas.util.utility.AtlasException;
import com.beo.atlas.util.utility.DateUtils;
import com.beo.atlas.util.utility.StringUtils;

/**
 * The Class ExportsExpdatMessageHandler. This class does the purpose of processing the message after
 * validation.controlls the save,send ,log and status
 * 
 * @author akhil.babu@beo.in
 * @version 1.0
 * @since Feb 9, 2018
 */
public class ExportsExpdatMessageHandler
{
	private static final AtlasLogManager LOG_MANAGER = new AtlasLogManager(ExportsExpdatMessageHandler.class);

	private static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	private static final String SAVE_LITERAL = "SAVE";
	private static final String EMPTY_LITERAL = "";
	private static final String YES = "yes";
	private static final String ATR_COUNTRY = "TR";
	private static final String PIPE_SYMBOL_WITH_AUS_LITERAL = "|AUS";
	private static final String PIPE_SYMBOL_WITH_EMF_LITERAL = "|EMF";
	private static final String PIPE_SYMBOL_WITH_ANM_LITERAL = "|ANM";
	private static final String PIPE_SYMBOL_WITH_VER_LITERAL = "|VER";
	private static final String PIPE_SYMBOL_WITH_SUB_LITERAL = "|SUB";
	private static final String PIPE_SYMBOL_WITH_VPV_LITERAL = "|VPV";
	private static final String PIPE_SYMBOL_WITH_POSEMF_LITERAL = "|PosEMF";
	private static final char PIPE_SYMBOL = '|';
	private static final String LITERAL_BLOCKED = "BLOCKED";

	private static final int FOUR = 4;
	private static final int ZERO = 0;

	private transient String sendZollOperationStatus = EMPTY_LITERAL;

	/**
	 * Instantiates a new exports expdat message handler.
	 */
	public ExportsExpdatMessageHandler()
	{
		super();
	}

	/**
	 * Lookup EXPORTS EXPDAT facade.
	 *
	 * @return the ExportsExpDatFacadeRemote instance
	 */
	private ExportsExpDatFacadeRemote lookupExportsExpdatFacade()
	{
		try
		{
			return (ExportsExpDatFacadeRemote) RemoteServiceLocator.getInstance()
					.lookUp(JNDIConstants.EXPORTSEXPDATFACADEREMOTE);
		}
		catch (final Exception exception)
		{
			LOG_MANAGER.error("Exception from ExportsExpdatMessageHandler.java in lookupDeviceDetailFaced() :",
					exception);
		}
		return null;
	}

	/**
	 * Method used to save message details in db , create out xml and perform xsd validation.Also create entry in status
	 * and log table. Also update status in rest interface table for REST interface message.
	 * <p>
	 * Perform zoll send operation if user opt to send message .
	 * </p>
	 * 
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal
	 * @modifiedDate May 17,2021
	 * 
	 * @param messageDto the message dto
	 * @return the status of the process
	 */
	public String processExpdatMesage(final ExportsExpdatMessageDTO messageDto, final boolean saveOrSendFlag)
	{
		try
		{
			if (Objects.nonNull(messageDto))
			{
				final String userId = messageDto.getUserId();
				final MessageIdentifierObject messageIdentifierObject =
						MessagesUniqueValuesFinder.getInstance().findExportsMessagesUniqueValues(userId);
				LOG_MANAGER.info("EXPDAT Send -> nachrich generation finished");
				final ExportsExpdatHeaderDTO exportsExpdatHeaderDto = messageDto.getExportsExpdatHeaderDto();
				exportsExpdatHeaderDto.setNachrichtennummer(messageIdentifierObject.getNachrichtennummer());
				messageDto.setInterchangeControlReference(messageIdentifierObject.getIcr());
				messageDto.setLaufendeNummer(messageIdentifierObject.getLaufendeNummer());
				this.removeIncorrectWarenortLadeortCode(exportsExpdatHeaderDto);
				final boolean result = saveExpdatMessage(messageDto);
				LOG_MANAGER.info("EXPDAT Send -> db save finished");
				if (result)
				{
					final String filePath = this.createXml(messageDto);
					LOG_MANAGER.info("EXPDAT Send -> xml creation finished");
					// Since this XSD validation checking is not required for live it is commented, and it is required
					// in certification time so don't delete the commented portion
					// final String validation = ExportsXmlFileValidator.getInstance()
					// .validateXML(ExportsXmlFileValidatorIntConstants.TYPE_EXPDAT, filePath);
					// if (StringUtils.isNotNullOrEmpty(validation))
					// {
					// /** To show xsd validation errors in server log */
					// LOG_MANAGER.info("#@@@ >>>XSD Validation Error:#@@@EXPDAT Message --" + messageDto.getUserId()
					// + "Nachrich" + messageDto.getExportsExpdatHeaderDto().getNachrichtennummer() + "XSD:>>"
					// + validation);
					//
					// }
					// else
					if (saveOrSendFlag && StringUtils.isNotNullOrEmpty(filePath))
					{
						this.sendZollOperationStatus = doOperationsToZoll(messageDto, filePath);
					}
					this.updateExportsStatusAndLog(messageDto, filePath);
					new ExportsMessageProcessHandlerHelper().updateRestInterfaceTableWithNachrich(messageDto,
							exportsExpdatHeaderDto.getNachrichtennummer(), userId);
				}
			}
			return this.sendZollOperationStatus;

		}
		catch (EJBException | PersistenceException e)
		{
			LOG_MANAGER.error("Error while saving expdat message", e);
			return EMPTY_LITERAL;
		}
	}

	/**
	 * Method used to remove incorrect warenort ladeort code from expdat header dto
	 *
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal
	 * @modifiedDate May 14,2021
	 * 
	 * @param exportsExpdatHeaderDto the exports expdat header dto
	 */
	private void removeIncorrectWarenortLadeortCode(final ExportsExpdatHeaderDTO exportsExpdatHeaderDto)
	{
		String warenOrtLadeortCode =
				StringUtils.getTrimValueAfterNullCheck(exportsExpdatHeaderDto.getWarenortLadeortCode());
		if (warenOrtLadeortCode.contains(ExportsExpDatMessageRuleConstants.HYPHEN))
		{
			final List<String> splittedList =
					StringUtils.getSplittedList(warenOrtLadeortCode, ExportsExpDatMessageRuleConstants.HYPHEN);
			warenOrtLadeortCode = StringUtils.getTrimValueAfterNullCheck(StringUtils.getListValue(splittedList, ZERO));
		}
		final boolean ladeortValidate = !exportsExpdatHeaderDto.isLadeortFieldValidate()
				&& !ExportsExpDatMessageRuleConstants.LADEORT_CODE_PATTERN.matcher(warenOrtLadeortCode).matches();
		if (ladeortValidate)
		{
			exportsExpdatHeaderDto.setWarenortLadeortCode(ExportsExpDatMessageRuleConstants.EMPTY_STRING);
		}
	}

	/**
	 * Process expdat mesage for epas . save message details in db
	 *
	 * @param messageDto the message dto
	 * @return the string
	 */
	public ExportsExpdatMessageDTO processExpdatMesageForEpas(final ExportsExpdatMessageDTO messageDto)
	{
		if (Objects.nonNull(messageDto) && StringUtils.isNotNullOrEmpty(messageDto.getUserId()))
		{
			final MessageIdentifierObject messageIdentifierObject =
					MessagesUniqueValuesFinder.getInstance().findExportsMessagesUniqueValues(messageDto.getUserId());
			messageDto.getExportsExpdatHeaderDto().setNachrichtennummer(messageIdentifierObject.getNachrichtennummer());
			saveExpdatMessage(messageDto);
		}
		return messageDto;
	}

	/**
	 * Save expdat message in DB.
	 *
	 * @param messageDTO the message DTO
	 * @return true, if successful
	 */
	private boolean saveExpdatMessage(final ExportsExpdatMessageDTO messageDTO)
	{
		try
		{
			final ExportsExpDatFacadeRemote expdatFacade = lookupExportsExpdatFacade();
			if (Objects.nonNull(expdatFacade))
			{
				return expdatFacade.saveExpdatMessage(messageDTO);
			}
		}
		catch (EJBException | PersistenceException | IOException | AtlasException e)
		{
			LOG_MANAGER.error("saveExpdatMessage", e);
		}
		return false;

	}

	/**
	 * Method to send the xml file to Zoll
	 * <p>
	 * First create zip file of xml and then send that zip file to zoll.
	 * </p>
	 *
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Mar 16,2020
	 * 
	 * @param messageDto the Exports Expdat Message DTO
	 * @param filePath the file path
	 * @return String, send status
	 */
	private String doOperationsToZoll(final ExportsExpdatMessageDTO messageDto, final String filePath)
	{
		String zollSendStatus = EMPTY_LITERAL;
		final String zipFilePath = ExportsCommonFileCreator.getInstance().createZipFile(filePath);
		LOG_MANAGER.info("EXPDAT Send -> zip creation finished");
		if (StringUtils.isNotNullOrEmpty(zipFilePath))
		{
			zollSendStatus = SendMessage.send(messageDto.getUserId(), "Ausfuhr", zipFilePath);
		}
		return zollSendStatus;
	}

	/**
	 * Creates the xml.
	 *
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal@beo.in
	 * @modifiedDate Nov 30,2020
	 * 
	 * @param messageDTO the message DTO
	 * @return the string
	 */
	private String createXml(final ExportsExpdatMessageDTO messageDTO)
	{
		final ExportsExpdatXmlCreator xmlCreator = new ExportsExpdatXmlCreator();
		messageDTO.setMessageTimeMilliSeconds(System.currentTimeMillis());
		return xmlCreator.createMessage(messageDTO);
	}

	/**
	 * Update exports status and log.
	 * <p>
	 * For interface messages , Also update expowinref id in exports status table . Also update nachrich nummer and
	 * send/save status in expowin atlas table
	 * </p>
	 *
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal@beo.in
	 * @modifiedDate Aug 02,2021
	 * 
	 * @param messageDTO the message DTO
	 * @param fileName the file name
	 * @return true, if successful
	 */
	private boolean updateExportsStatusAndLog(final ExportsExpdatMessageDTO messageDTO, final String fileName)
	{
		try
		{
			messageDTO.setProcessingStatus(getStatus());
			final ExportsLogDTO logDto = getExportsLogDto(messageDTO, fileName);
			final ExportsLogFacadeRemote exportsLogFacade = lookUpExportsLogFacade();

			if (Objects.nonNull(exportsLogFacade))
			{
				exportsLogFacade.saveExportsLog(logDto);
			}
			final ExportsStatusDTO exportsStatusDto = getExportsStatusDto(messageDTO, fileName);
			final ExpowinAtlasFacadeRemote expowinAtlasFacade = lookUpExpowinAtlasFacade();
			final String nachrichtennummer = messageDTO.getExportsExpdatHeaderDto().getNachrichtennummer();
			if (Objects.nonNull(expowinAtlasFacade) && messageDTO.isIntefaceMessage())
			{
				expowinAtlasFacade.updateExpowinAfterSend(messageDTO.getExpowinId(), messageDTO.getUserId(),
						nachrichtennummer, messageDTO.getProcessingStatus());
				exportsStatusDto.setExpowinRef(String.valueOf(messageDTO.getExpowinId()));
			}
			else if (Objects.nonNull(expowinAtlasFacade) && messageDTO.isExpowinSaved())
			{
				expowinAtlasFacade.clearExpowinFileColumnsAndUpdateExpowinForSave(messageDTO.getUserId(),
						messageDTO.getExpowinId(), nachrichtennummer, messageDTO.getProcessingStatus());
				exportsStatusDto.setExpowinRef(String.valueOf(messageDTO.getExpowinId()));
			}
			else
			{
				exportsStatusDto.setExpowinRef(EMPTY_LITERAL);
			}
			final ExportsStatusFacadeRemote exportsStatusFacade = lookUpExportsStatusFacade();
			if (Objects.nonNull(exportsStatusFacade))
			{
				exportsStatusFacade.saveExportsStatus(exportsStatusDto);
			}
			return true;
		}
		catch (EJBException | PersistenceException | IOException e)
		{
			LOG_MANAGER.error("Exception from  updateExportsStatusAndLog() :ExportsExpdatMessageHandler", e);
			return false;

		}
	}

	/**
	 * Gets the exports log dto by adding required details to update.
	 *
	 * @author akhil.babu@beo.in
	 * @modifiedBy laya.venugopal
	 * @modifiedDate Nov 30, 2020
	 * 
	 * @param messageDTO the message DTO
	 * @param fileName the file name
	 * @param status the status
	 * @return the exports log dto
	 */
	private ExportsLogDTO getExportsLogDto(final ExportsExpdatMessageDTO messageDTO, final String fileName)
	{
		final ExportsExpdatHeaderDTO headerDto = messageDTO.getExportsExpdatHeaderDto();
		final ExportsLogDTO logDto = new ExportsLogDTO();
		logDto.setLogid(headerDto.getNachrichtennummer());
		logDto.setUserid(messageDTO.getUserId());
		logDto.setDatum(DateUtils.getFormattedDateTimeForGivenEpochMilliSeconds(messageDTO.getMessageTimeMilliSeconds(),
				FORMAT_YYYY_MM_DD_HH_MM_SS));
		logDto.setDoctype(messageDTO.getArtder());
		logDto.setDocref(messageDTO.getExportsExpdatHeaderDto().getBezugsnummer());
		logDto.setRegno(EMPTY_LITERAL);
		logDto.setNachrich(headerDto.getNachrichtennummer());
		logDto.setRetnachrich(EMPTY_LITERAL);
		logDto.setArtder(messageDTO.getArtder());
		logDto.setFilename(fileName);
		logDto.setVersion(messageDTO.getVersion());
		logDto.setStatus(messageDTO.getProcessingStatus());
		logDto.setInfo(EMPTY_LITERAL);
		logDto.setMailaction("S");
		logDto.setSuser(messageDTO.getSubUserId());
		logDto.setDisplay("Y");
		logDto.setMrn(headerDto.getMrn());
		final List<String> tinEori = StringUtils
				.getSplittedList(StringUtils.getValueAfterNullCheck(headerDto.getEoriNiederlassungsnummer()), "||");
		logDto.setEori(StringUtils.getListValue(tinEori, ZERO));
		logDto.setBin(EMPTY_LITERAL);
		logDto.setNachGroup(messageDTO.getNachrichGruppe());
		return logDto;

	}

	/**
	 * Gets the exports status dto by adding required details to update.
	 * 
	 * @author akhil.babu@beo.in
	 * @modifiedBy rajeev.k
	 * @modifiedDate Nov 27,2020
	 *
	 * @param messageDTO the message DTO
	 * @param fileName the file name
	 * @param status the status
	 * @return the exports status dto
	 */
	private ExportsStatusDTO getExportsStatusDto(final ExportsExpdatMessageDTO messageDTO, final String fileName)
	{
		final ExportsExpdatHeaderDTO exportsExpdatHeaderDto = messageDTO.getExportsExpdatHeaderDto();
		final ExportsStatusDTO statusDto = new ExportsStatusDTO();
		statusDto.setUserid(messageDTO.getUserId());
		statusDto.setNachrich(exportsExpdatHeaderDto.getNachrichtennummer());
		statusDto.setArtder(messageDTO.getArtder());
		final String formattedCurrentDateTime = DateUtils.getFormattedDateTimeForGivenEpochMilliSeconds(
				messageDTO.getMessageTimeMilliSeconds(), FORMAT_YYYY_MM_DD_HH_MM_SS);
		statusDto.setDatum(formattedCurrentDateTime);
		statusDto.setDocref(exportsExpdatHeaderDto.getBezugsnummer());
		statusDto.setBearbeiter(EMPTY_LITERAL);
		statusDto.setRegno(EMPTY_LITERAL);
		statusDto.setVersion(messageDTO.getVersion());
		statusDto.setMailcode(EMPTY_LITERAL);
		statusDto.setFilename(fileName);
		statusDto.setInfo(EMPTY_LITERAL);
		statusDto.setStatus(messageDTO.getProcessingStatus());
		statusDto.setDisplay("Y");
		statusDto.setMrn(exportsExpdatHeaderDto.getMrn());
		statusDto.setVersandDatum(formattedCurrentDateTime);
		statusDto.setSubUser(messageDTO.getSubUserId());
		statusDto.setNachGroup(messageDTO.getNachrichGruppe());
		statusDto.setSendMsgType(EMPTY_LITERAL);
		final ExportsAddressDTO empfanger = exportsExpdatHeaderDto.getEmpfanger();
		statusDto.setEmfanger(StringUtils.getTrimValueAfterNullCheck(empfanger.getName()));
		if (!StringUtils.isNotNullOrEmpty(statusDto.getEmfanger()))
		{
			statusDto.setEmfanger(StringUtils.getTrimValueAfterNullCheck(empfanger.getTin()));
		}
		statusDto.setSankSprufung(StringUtils.getTrimValueAfterNullCheck(exportsExpdatHeaderDto.getSanktionstatus()));
		statusDto.setUrsStatus(EMPTY_LITERAL);
		statusDto.setAtrStatus(EMPTY_LITERAL);
		statusDto.setEurStatus(EMPTY_LITERAL);

		final String addressIds = getMergedAddressIds(messageDTO);
		statusDto.setAddressIds(addressIds);
		final String bestimmungsLand =
				StringUtils.getTrimValueAfterNullCheck(exportsExpdatHeaderDto.getBestimmungsLand());
		if (StringUtils.isNotNullOrEmpty(bestimmungsLand))
		{
			if (messageDTO.isUrsStatus())
			{
				statusDto.setUrsStatus(YES);
			}
			if (messageDTO.isUrsStatus() || messageDTO.isEurStatus() || messageDTO.isAtrStatus())
			{
				statusDto.setEurStatus(YES);
				if (ATR_COUNTRY.equals(bestimmungsLand))
				{
					statusDto.setAtrStatus(YES);
				}
			}
		}
		final String warenortLadeortCode =
				StringUtils.substringValue(exportsExpdatHeaderDto.getWarenortLadeortCode(), ZERO, FOUR);
		statusDto.setLadeortCode(warenortLadeortCode);
		statusDto.setKennumerSendung(exportsExpdatHeaderDto.getKennnummerderSendung());
		return statusDto;
	}

	/**
	 * Gets the exports address Ids used in message to update in db.
	 * 
	 * @author ajeesh.mathew@beo.in
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate July 14,2020
	 *
	 * @param messageDTO the message DTO
	 * @return the exports address Ids
	 */
	private String getMergedAddressIds(final ExportsExpdatMessageDTO messageDTO)
	{
		final StringBuilder addressIds = new StringBuilder();

		final List<ExportsExpdatPositionDTO> exportsExpdatPositionDtoList = messageDTO.getExportsExpdatPositionDto();

		for (final ExportsExpdatPositionDTO exportsExpdatPositionDto : exportsExpdatPositionDtoList)
		{
			if (Objects.nonNull(exportsExpdatPositionDto.getEmpfanger())
					&& ZERO != exportsExpdatPositionDto.getEmpfanger().getId())
			{
				addressIds.append(PIPE_SYMBOL_WITH_POSEMF_LITERAL);
				addressIds.append(exportsExpdatPositionDto.getEmpfanger().getId());
			}
		}

		final ExportsExpdatHeaderDTO exportsExpdatHeaderDto = messageDTO.getExportsExpdatHeaderDto();

		if (Objects.nonNull(exportsExpdatHeaderDto.getAusfuhrer())
				&& ZERO != exportsExpdatHeaderDto.getAusfuhrer().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_AUS_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getAusfuhrer().getId());
		}

		if (Objects.nonNull(exportsExpdatHeaderDto.getEmpfanger())
				&& ZERO != exportsExpdatHeaderDto.getEmpfanger().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_EMF_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getEmpfanger().getId());
		}

		getHeaderMergedAddressIds(addressIds, exportsExpdatHeaderDto);
		return addressIds.toString();
	}

	/**
	 * Gets the exports header address Ids used in message.
	 * 
	 * @author ajeesh.mathew@beo.in
	 * @modifiedBy ajeesh.mathew
	 * @modifiedDate July 14,2020
	 *
	 * @param addressIds the address ids
	 * @param exportsExpdatHeaderDto the exports expdat header DTO
	 */
	private void getHeaderMergedAddressIds(final StringBuilder addressIds,
			final ExportsExpdatHeaderDTO exportsExpdatHeaderDto)
	{
		if (Objects.nonNull(exportsExpdatHeaderDto.getAnmelder())
				&& ZERO != exportsExpdatHeaderDto.getAnmelder().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_ANM_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getAnmelder().getId());
		}

		if (Objects.nonNull(exportsExpdatHeaderDto.getVertreterAnmelders())
				&& ZERO != exportsExpdatHeaderDto.getVertreterAnmelders().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_VER_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getVertreterAnmelders().getId());
		}

		if (Objects.nonNull(exportsExpdatHeaderDto.getSubunternehmer())
				&& ZERO != exportsExpdatHeaderDto.getSubunternehmer().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_SUB_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getSubunternehmer().getId());
		}

		if (Objects.nonNull(exportsExpdatHeaderDto.getVerfahrensinhaberPv())
				&& ZERO != exportsExpdatHeaderDto.getVerfahrensinhaberPv().getId())
		{
			addressIds.append(PIPE_SYMBOL_WITH_VPV_LITERAL);
			addressIds.append(exportsExpdatHeaderDto.getVerfahrensinhaberPv().getId());
		}

		if (StringUtils.isNotNullOrEmpty(addressIds.toString()))
		{
			addressIds.append(PIPE_SYMBOL);
		}
	}

	/**
	 * Look up exports log facade.
	 *
	 * @return the exports log facade remote
	 */
	private ExportsLogFacadeRemote lookUpExportsLogFacade()
	{
		try
		{
			return (ExportsLogFacadeRemote) RemoteServiceLocator.getInstance()
					.lookUp(JNDIConstants.EXPORTSLOGFACADEREMOTE);
		}
		catch (final Exception exception)
		{
			LOG_MANAGER.error("lookUpExportsLogFacade()", exception);
		}
		return null;
	}

	/**
	 * Look up exports status facade.
	 *
	 * @return the exports status facade remote
	 */
	private ExportsStatusFacadeRemote lookUpExportsStatusFacade()
	{
		try
		{
			return (ExportsStatusFacadeRemote) RemoteServiceLocator.getInstance()
					.lookUp(JNDIConstants.EXPORTSSTATUSFACADEREMOTE);
		}
		catch (final Exception exception)
		{
			LOG_MANAGER.error("lookUpExportsStatusFacade()", exception);
		}
		return null;
	}

	/**
	 * Look up expowin atlas facade.
	 *
	 * @author arun.nair@beo.in
	 * @modifiedBy
	 * @modifiedDate Feb 12 2019
	 * @return the expowin atlas facade remote
	 */
	private ExpowinAtlasFacadeRemote lookUpExpowinAtlasFacade()
	{
		try
		{
			return (ExpowinAtlasFacadeRemote) RemoteServiceLocator.getInstance()
					.lookUp(JNDIConstants.EXPOWINATLASFACADEREMOTE);
		}
		catch (final Exception exception)
		{
			LOG_MANAGER.error("lookUpExpowinAtlasFacade()", exception);
		}
		return null;
	}

	/**
	 * Method used to get the status for saving in log and status table
	 *
	 * @author arun.nair@beo.in
	 * @modifiedBy surya.ms
	 * @modifiedDate Oct 14,2020
	 * 
	 * @return the status
	 */
	private String getStatus()
	{
		String status = SAVE_LITERAL;
		if (StringUtils.isNotNullOrEmpty(this.sendZollOperationStatus)
				&& !LITERAL_BLOCKED.equals(this.sendZollOperationStatus))
		{
			status = this.sendZollOperationStatus;
		}
		return status;
	}

}
