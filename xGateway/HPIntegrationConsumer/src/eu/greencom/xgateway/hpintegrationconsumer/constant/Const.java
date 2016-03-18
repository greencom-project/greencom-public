package eu.greencom.xgateway.hpintegrationconsumer.constant;

import java.util.HashMap;

public class Const {

	public static final int u8 = 0;
	public static final int u16 = 1;
	public static final int u32 = 2;
	public static final int s8 = 3;
	public static final int s16 = 4;
	public static final int s32 = 5;
	// ----------------------------------------------------------------------------

	public static final int BT1Outdoortemp = 40004; // s16 , mode = R
	public static final int BT1Outdoortemp_type = s16;
	public static final int EB23BT2SupplytempS4 = 40005; // s16 , mode = R
	public static final int EB23BT2SupplytempS4_type = s16;
	public static final int EB22BT2SupplytempS3 = 40006; // s16 , mode = R
	public static final int EB22BT2SupplytempS3_type = s16;
	public static final int EB21BT2SupplytempS2 = 40007; // s16 , mode = R
	public static final int EB21BT2SupplytempS2_type = s16;
	public static final int BT2SupplytempS1 = 40008; // s16 , mode = R
	public static final int BT2SupplytempS1_type = s16;
	public static final int EB100EP14BT3Returntemp = 40012; // s16 , mode = R
	public static final int EB100EP14BT3Returntemp_type = s16;
	public static final int BT7HotWatertop = 40013; // s16 , mode = R
	public static final int BT7HotWatertop_type = s16;
	public static final int BT6HotWaterload = 40014; // s16 , mode = R
	public static final int BT6HotWaterload_type = s16;
	public static final int EB100EP14BT17Suction = 40022; // s16 , mode = R
	public static final int EB100EP14BT17Suction_type = s16;
	public static final int EB23BT50RoomTempS4 = 40030; // s16 , mode = R
	public static final int EB23BT50RoomTempS4_type = s16;
	public static final int EB22BT50RoomTempS3 = 40031; // s16 , mode = R
	public static final int EB22BT50RoomTempS3_type = s16;
	public static final int EB21BT50RoomTempS2 = 40032; // s16 , mode = R
	public static final int EB21BT50RoomTempS2_type = s16;
	public static final int BT50RoomTempS1 = 40033; // s16 , mode = R
	public static final int BT50RoomTempS1_type = s16;
	public static final int EP8BT53SolarPanelTemp = 40043; // s16 , mode = R
	public static final int EP8BT53SolarPanelTemp_type = s16;
	public static final int EP8BT54SolarLoadTemp = 40044; // s16 , mode = R
	public static final int EP8BT54SolarLoadTemp_type = s16;
	public static final int EQ1BT64PCS4SupplyTemp = 40045; // s16 , mode = R
	public static final int EQ1BT64PCS4SupplyTemp_type = s16;
	public static final int EB100FD1Temperaturelimiter = 40054; // s16 , mode =
																// R
	public static final int EB100FD1Temperaturelimiter_type = s16;
	public static final int BT1Average = 40067; // s16 , mode = R
	public static final int BT1Average_type = s16;
	public static final int EM1BT52Boilertemperature = 40070; // s16 , mode = R
	public static final int EM1BT52Boilertemperature_type = s16;
	public static final int BF1Flow = 40072; // s16 , mode = R
	public static final int BF1Flow_type = s16;
	public static final int EB100FR1AnodeStatus = 40074; // s16 , mode = R
	public static final int EB100FR1AnodeStatus_type = s16;
	public static final int EB100BE3CurrentPhase3 = 40079; // s32 , mode = R
	public static final int EB100BE3CurrentPhase3_type = s32;
	public static final int EB100BE2CurrentPhase2 = 40081; // s32 , mode = R
	public static final int EB100BE2CurrentPhase2_type = s32;
	public static final int EB100BE1CurrentPhase1 = 40083; // s32 , mode = R
	public static final int EB100BE1CurrentPhase1_type = s32;
	public static final int BT63AddSupplyTemp = 40121; // s16 , mode = R
	public static final int BT63AddSupplyTemp_type = s16;
	public static final int EB23BT3ReturntempS4 = 40127; // s16 , mode = R
	public static final int EB23BT3ReturntempS4_type = s16;
	public static final int EB22BT3ReturntempS3 = 40128; // s16 , mode = R
	public static final int EB22BT3ReturntempS3_type = s16;
	public static final int EB21BT3ReturntempS2 = 40129; // s16 , mode = R
	public static final int EB21BT3ReturntempS2_type = s16;
	public static final int EP8BT51SolarpoolTemp = 40154; // s16 , mode = R
	public static final int EP8BT51SolarpoolTemp_type = s16;
	public static final int Softwareversion = 43001; // u16 , mode = R
	public static final int Softwareversion_type = u16;
	public static final int DegreeMinutes = 43005; // s16 , mode = R/W
	public static final int DegreeMinutes_type = s16;
	public static final int CalculatedSupplyTemperatureS4 = 43006; // s16 , mode
																	// = R
	public static final int CalculatedSupplyTemperatureS4_type = s16;
	public static final int CalculatedSupplyTemperatureS3 = 43007; // s16 , mode
																	// = R
	public static final int CalculatedSupplyTemperatureS3_type = s16;
	public static final int CalculatedSupplyTemperatureS2 = 43008; // s16 , mode
																	// = R
	public static final int CalculatedSupplyTemperatureS2_type = s16;
	public static final int CalculatedSupplyTemperatureS1 = 43009; // s16 , mode
																	// = R
	public static final int CalculatedSupplyTemperatureS1_type = s16;
	public static final int FreezeProtectionStatus = 43013; // u8 , mode = R
	public static final int FreezeProtectionStatus_type = u8;
	public static final int StatusCooling = 43024; // u8 , mode = R
	public static final int StatusCooling_type = u8;
	public static final int Totoptimeadd = 43081; // s32 , mode = R
	public static final int Totoptimeadd_type = s32;
	public static final int InteladdPower = 43084; // s16 , mode = R
	public static final int InteladdPower_type = s16;
	public static final int Prio = 43086; // u8 , mode = R
	public static final int Prio_type = u8;
	public static final int InteladdState = 43091; // u8 , mode = R
	public static final int InteladdState_type = u8;
	public static final int Accumulatedenergy = 43230; // u32 , mode = R
	public static final int Accumulatedenergy_type = u32;
	public static final int TotHWoptimeadd = 43239; // s32 , mode = R
	public static final int TotHWoptimeadd_type = s32;
	public static final int CompressorstartsEB100EP14 = 43416; // s32 , mode = R
	public static final int CompressorstartsEB100EP14_type = s32;
	public static final int TotoptimecomprEB100EP14 = 43420; // s32 , mode = R
	public static final int TotoptimecomprEB100EP14_type = s32;
	public static final int TotHWoptimecomprEB100EP14 = 43424; // s32 , mode = R
	public static final int TotHWoptimecomprEB100EP14_type = s32;
	public static final int CompressorStateEP14 = 43427; // u8 , mode = R
	public static final int CompressorStateEP14_type = u8;
	public static final int CompressorstatusEP14 = 43435; // u8 , mode = R
	public static final int CompressorstatusEP14_type = u8;
	public static final int CededOUeffect = 43459; // u16 , mode = R
	public static final int CededOUeffect_type = u16;
	public static final int Stepsextadd = 43490; // u8 , mode = R
	public static final int Stepsextadd_type = u8;
	public static final int PCABaseRelaysEP14 = 43514; // u8 , mode = R
	public static final int PCABaseRelaysEP14_type = u8;
	public static final int PCAPowerRelaysEP14 = 43516; // u8 , mode = R
	public static final int PCAPowerRelaysEP14_type = u8;
	public static final int EB101EP14BT3Returntemp = 44055; // s16 , mode = R
	public static final int EB101EP14BT3Returntemp_type = s16;
	public static final int EB101EP14BT12Condout = 44058; // s16 , mode = R
	public static final int EB101EP14BT12Condout_type = s16;
	public static final int EB101EP14BT14Hotgastemp = 44059; // s16 , mode = R
	public static final int EB101EP14BT14Hotgastemp_type = s16;
	public static final int EB101EP14BT15Liquidline = 44060; // s16 , mode = R
	public static final int EB101EP14BT15Liquidline_type = s16;
	public static final int EB101EP14BT17Suction = 44061; // s16 , mode = R
	public static final int EB101EP14BT17Suction_type = s16;
	public static final int CoolDegreeMinutes = 44266; // s16 , mode = R/W
	public static final int CoolDegreeMinutes_type = s16;
	public static final int CalcCoolingSupplyTemperatureS4 = 44267; // s16 ,
																	// mode = R
	public static final int CalcCoolingSupplyTemperatureS4_type = s16;
	public static final int CalcCoolingSupplyTemperatureS3 = 44268; // s16 ,
																	// mode = R
	public static final int CalcCoolingSupplyTemperatureS3_type = s16;
	public static final int CalcCoolingSupplyTemperatureS2 = 44269; // s16 ,
																	// mode = R
	public static final int CalcCoolingSupplyTemperatureS2_type = s16;
	public static final int CalcCoolingSupplyTemperatureS1 = 44270; // s16 ,
																	// mode = R
	public static final int CalcCoolingSupplyTemperatureS1_type = s16;
	public static final int Accumulatedenergyparts0 = 44298; // u32 , mode = R
	public static final int Accumulatedenergyparts_type0 = u32;
	public static final int Accumulatedenergyparts1 = 44300; // u32 , mode = R
	public static final int Accumulatedenergyparts_type1 = u32;
	public static final int Accumulatedenergyparts2 = 44302; // u32 , mode = R
	public static final int Accumulatedenergyparts_type2 = u32;
	public static final int Accumulatedenergyparts3 = 44304; // u32 , mode = R
	public static final int Accumulatedenergyparts_type3 = u32;
	public static final int Accumulatedenergyparts4 = 44306; // u32 , mode = R
	public static final int Accumulatedenergyparts_type4 = u32;
	public static final int Accumulatedenergyparts5 = 44308; // u32 , mode = R
	public static final int Accumulatedenergyparts_type5 = u32;
	public static final int calcoucompressorfreq = 44316; // u8 , mode = R
	public static final int calcoucompressorfreq_type = u8;
	public static final int SpeedextcoolingpumpGP15 = 44322; // s8 , mode = R
	public static final int SpeedextcoolingpumpGP15_type = s8;
	public static final int Softwarerelease = 44331; // u8 , mode = R
	public static final int Softwarerelease_type = u8;
	public static final int EB101EP14BT28Outdoortemp = 44362; // s16 , mode = R
	public static final int EB101EP14BT28Outdoortemp_type = s16;
	public static final int EB101EP14BT16Evaporator = 44363; // s16 , mode = R
	public static final int EB101EP14BT16Evaporator_type = s16;
	public static final int Alarmnumber = 45001; // s16 , mode = R
	public static final int Alarmnumber_type = s16;
	public static final int HWchargeoffset = 47062; // s8 , mode = R/W
	public static final int HWchargeoffset_type = s8;
	public static final int Floordryingtimer = 47291; // u16 , mode = R
	public static final int Floordryingtimer_type = u16;
	public static final int Stateextadd = 47633; // u8 , mode = R
	public static final int Stateextadd_type = u8;
	public static final int Speedheatmediumpump = 48102; // u16 , mode = R
	public static final int Speedheatmediumpump_type = u16;
	public static final int Speedchargepump = 48103; // u16 , mode = R
	public static final int Speedchargepump_type = u16;
	public static final int Coolingpumpmanualspeed = 44323; // s8 , mode = R/W
	public static final int Coolingpumpmanualspeed_type = s8;
	public static final int HeatcurveS4 = 47004; // s8 , mode = R/W
	public static final int HeatcurveS4_type = s8;
	public static final int HeatcurveS3 = 47005; // s8 , mode = R/W
	public static final int HeatcurveS3_type = s8;
	public static final int HeatcurveS2 = 47006; // s8 , mode = R/W
	public static final int HeatcurveS2_type = s8;
	public static final int HeatcurveS1 = 47007; // s8 , mode = R/W
	public static final int HeatcurveS1_type = s8;
	public static final int OffsetS4 = 47008; // s8 , mode = R/W
	public static final int OffsetS4_type = s8;
	public static final int OffsetS3 = 47009; // s8 , mode = R/W
	public static final int OffsetS3_type = s8;
	public static final int OffsetS2 = 47010; // s8 , mode = R/W
	public static final int OffsetS2_type = s8;
	public static final int OffsetS1 = 47011; // s8 , mode = R/W
	public static final int OffsetS1_type = s8;
	public static final int MinSupplySystem4 = 47012; // s16 , mode = R/W
	public static final int MinSupplySystem4_type = s16;
	public static final int MinSupplySystem3 = 47013; // s16 , mode = R/W
	public static final int MinSupplySystem3_type = s16;
	public static final int MinSupplySystem2 = 47014; // s16 , mode = R/W
	public static final int MinSupplySystem2_type = s16;
	public static final int MinSupplySystem1 = 47015; // s16 , mode = R/W
	public static final int MinSupplySystem1_type = s16;
	public static final int MaxSupplySystem4 = 47016; // s16 , mode = R/W
	public static final int MaxSupplySystem4_type = s16;
	public static final int MaxSupplySystem3 = 47017; // s16 , mode = R/W
	public static final int MaxSupplySystem3_type = s16;
	public static final int MaxSupplySystem2 = 47018; // s16 , mode = R/W
	public static final int MaxSupplySystem2_type = s16;
	public static final int MaxSupplySystem1 = 47019; // s16 , mode = R/W
	public static final int MaxSupplySystem1_type = s16;
	public static final int OwnCurveP7 = 47020; // s8 , mode = R/W
	public static final int OwnCurveP7_type = s8;
	public static final int OwnCurveP6 = 47021; // s8 , mode = R/W
	public static final int OwnCurveP6_type = s8;
	public static final int OwnCurveP5 = 47022; // s8 , mode = R/W
	public static final int OwnCurveP5_type = s8;
	public static final int OwnCurveP4 = 47023; // s8 , mode = R/W
	public static final int OwnCurveP4_type = s8;
	public static final int OwnCurveP3 = 47024; // s8 , mode = R/W
	public static final int OwnCurveP3_type = s8;
	public static final int OwnCurveP2 = 47025; // s8 , mode = R/W
	public static final int OwnCurveP2_type = s8;
	public static final int OwnCurveP1 = 47026; // s8 , mode = R/W
	public static final int OwnCurveP1_type = s8;
	public static final int Pointoffsetoutdoortemp = 47027; // s8 , mode = R/W
	public static final int Pointoffsetoutdoortemp_type = s8;
	public static final int Pointoffset = 47028; // s8 , mode = R/W
	public static final int Pointoffset_type = s8;
	public static final int ExternaladjustmentS4 = 47029; // s8 , mode = R/W
	public static final int ExternaladjustmentS4_type = s8;
	public static final int ExternaladjustmentS3 = 47030; // s8 , mode = R/W
	public static final int ExternaladjustmentS3_type = s8;
	public static final int ExternaladjustmentS2 = 47031; // s8 , mode = R/W
	public static final int ExternaladjustmentS2_type = s8;
	public static final int ExternaladjustmentS1 = 47032; // s8 , mode = R/W
	public static final int ExternaladjustmentS1_type = s8;
	public static final int ExternaladjustmentwithroomsensorS4 = 47033; // s16 ,
																		// mode
																		// =
	// R/W
	public static final int ExternaladjustmentwithroomsensorS4_type = s16;
	public static final int ExternaladjustmentwithroomsensorS3 = 47034; // s16 ,
																		// mode
																		// =
	// R/W
	public static final int ExternaladjustmentwithroomsensorS3_type = s16;
	public static final int ExternaladjustmentwithroomsensorS2 = 47035; // s16 ,
																		// mode
																		// =
	// R/W
	public static final int ExternaladjustmentwithroomsensorS2_type = s16;
	public static final int ExternaladjustmentwithroomsensorS1 = 47036; // s16 ,
																		// mode
																		// =
	// R/W
	public static final int ExternaladjustmentwithroomsensorS1_type = s16;
	public static final int Hotwatermode = 47041; // s8 , mode = R/W
	public static final int Hotwatermode_type = s8;
	public static final int StarttemperatureHWLuxury = 47043; // s16 , mode =
																// R/W
	public static final int StarttemperatureHWLuxury_type = s16;
	public static final int StarttemperatureHWNormal = 47044; // s16 , mode =
																// R/W
	public static final int StarttemperatureHWNormal_type = s16;
	public static final int StarttemperatureHWEconomy = 47045; // s16 , mode =
																// R/W
	public static final int StarttemperatureHWEconomy_type = s16;
	public static final int StoptemperaturePeriodicHW = 47046; // s16 , mode =
																// R/W
	public static final int StoptemperaturePeriodicHW_type = s16;
	public static final int StoptemperatureHWLuxury = 47047; // s16 , mode = R/W
	public static final int StoptemperatureHWLuxury_type = s16;
	public static final int StoptemperatureHWNormal = 47048; // s16 , mode = R/W
	public static final int StoptemperatureHWNormal_type = s16;
	public static final int StoptemperatureHWEconomy = 47049; // s16 , mode =
																// R/W
	public static final int StoptemperatureHWEconomy_type = s16;
	public static final int RuntimeHWC = 47054; // s8 , mode = R/W
	public static final int RuntimeHWC_type = s8;
	public static final int StilltimeHWC = 47055; // s8 , mode = R/W
	public static final int StilltimeHWC_type = s8;
	public static final int Language = 47131; // s8 , mode = R/W
	public static final int Language_type = s8;
	public static final int PeriodHW = 47134; // u8 , mode = R/W
	public static final int PeriodHW_type = u8;
	public static final int PeriodHeat = 47135; // u8 , mode = R/W
	public static final int PeriodHeat_type = u8;
	public static final int PeriodPool = 47136; // u8 , mode = R/W
	public static final int PeriodPool_type = u8;
	public static final int Operationalmodeheatmediumpump = 47138; // u8 , mode
																	// = R/W
	public static final int Operationalmodeheatmediumpump_type = u8;
	public static final int DMstartheating = 47206; // s16 , mode = R/W
	public static final int DMstartheating_type = s16;
	public static final int DMstartcooling = 47207; // s16 , mode = R/W
	public static final int DMstartcooling_type = s16;
	public static final int DMstartadd = 47208; // s16 , mode = R/W
	public static final int DMstartadd_type = s16;
	public static final int DMbetweenaddsteps = 47209; // s16 , mode = R/W
	public static final int DMbetweenaddsteps_type = s16;
	public static final int Maxintaddpower = 47212; // s16 , mode = R/W
	public static final int Maxintaddpower_type = s16;
	public static final int Fuse = 47214; // u8 , mode = R/W
	public static final int Fuse_type = u8;
	public static final int Floordrying = 47276; // u8 , mode = R/W
	public static final int Floordrying_type = u8;
	public static final int Floordryingperiod7 = 47277; // u8 , mode = R/W
	public static final int Floordryingperiod7_type = u8;
	public static final int Floordryingperiod6 = 47278; // u8 , mode = R/W
	public static final int Floordryingperiod6_type = u8;
	public static final int Floordryingperiod5 = 47279; // u8 , mode = R/W
	public static final int Floordryingperiod5_type = u8;
	public static final int Floordryingperiod4 = 47280; // u8 , mode = R/W
	public static final int Floordryingperiod4_type = u8;
	public static final int Floordryingperiod3 = 47281; // u8 , mode = R/W
	public static final int Floordryingperiod3_type = u8;
	public static final int Floordryingperiod2 = 47282; // u8 , mode = R/W
	public static final int Floordryingperiod2_type = u8;
	public static final int Floordryingperiod1 = 47283; // u8 , mode = R/W
	public static final int Floordryingperiod1_type = u8;
	public static final int Floordryingtemp7 = 47284; // u8 , mode = R/W
	public static final int Floordryingtemp7_type = u8;
	public static final int Floordryingtemp6 = 47285; // u8 , mode = R/W
	public static final int Floordryingtemp6_type = u8;
	public static final int Floordryingtemp5 = 47286; // u8 , mode = R/W
	public static final int Floordryingtemp5_type = u8;
	public static final int Floordryingtemp4 = 47287; // u8 , mode = R/W
	public static final int Floordryingtemp4_type = u8;
	public static final int Floordryingtemp3 = 47288; // u8 , mode = R/W
	public static final int Floordryingtemp3_type = u8;
	public static final int Floordryingtemp2 = 47289; // u8 , mode = R/W
	public static final int Floordryingtemp2_type = u8;
	public static final int Floordryingtemp1 = 47290; // u8 , mode = R/W
	public static final int Floordryingtemp1_type = u8;
	public static final int DOT = 47300; // s16 , mode = R/W
	public static final int DOT_type = s16;
	public static final int deltaTatDOT = 47301; // s16 , mode = R/W
	public static final int deltaTatDOT_type = s16;
	public static final int Climatesystem2accessory = 47302; // u8 , mode = R/W
	public static final int Climatesystem2accessory_type = u8;
	public static final int Climatesystem3accessory = 47303; // u8 , mode = R/W
	public static final int Climatesystem3accessory_type = u8;
	public static final int Climatesystem4accessory = 47304; // u8 , mode = R/W
	public static final int Climatesystem4accessory_type = u8;
	public static final int Climatesystem4mixingvalveamp = 47305; // s8 , mode =
																	// R/W
	public static final int Climatesystem4mixingvalveamp_type = s8;
	public static final int Climatesystem3mixingvalveamp = 47306; // s8 , mode =
																	// R/W
	public static final int Climatesystem3mixingvalveamp_type = s8;
	public static final int Climatesystem2mixingvalveamp = 47307; // s8 , mode =
																	// R/W
	public static final int Climatesystem2mixingvalveamp_type = s8;
	public static final int Climatesystem4shuntwait = 47308; // s16 , mode = R/W
	public static final int Climatesystem4shuntwait_type = s16;
	public static final int Climatesystem3shuntwait = 47309; // s16 , mode = R/W
	public static final int Climatesystem3shuntwait_type = s16;
	public static final int Climatesystem2shuntwait = 47310; // s16 , mode = R/W
	public static final int Climatesystem2shuntwait_type = s16;
	public static final int Timebetwswitchheatcool = 47335; // s8 , mode = R/W
	public static final int Timebetwswitchheatcool_type = s8;
	public static final int Heatatroomundertemp = 47336; // s8 , mode = R/W
	public static final int Heatatroomundertemp_type = s8;
	public static final int Coolatroomovertemp = 47337; // s8 , mode = R/W
	public static final int Coolatroomovertemp_type = s8;
	public static final int Coolingmixvalvestepdelay = 47339; // s16 , mode =
																// R/W
	public static final int Coolingmixvalvestepdelay_type = s16;
	public static final int SMS40accessory = 47352; // u8 , mode = R/W
	public static final int SMS40accessory_type = u8;
	public static final int AllowAdditiveHeating = 47370; // u8 , mode = R/W
	public static final int AllowAdditiveHeating_type = u8;
	public static final int AllowHeating = 47371; // u8 , mode = R/W
	public static final int AllowHeating_type = u8;
	public static final int AllowCooling = 47372; // u8 , mode = R/W
	public static final int AllowCooling_type = u8;
	public static final int Maxdiffcomp = 47378; // s16 , mode = R/W
	public static final int Maxdiffcomp_type = s16;
	public static final int Maxdiffadd = 47379; // s16 , mode = R/W
	public static final int Maxdiffadd_type = s16;
	public static final int Dateformat = 47384; // u8 , mode = R/W
	public static final int Dateformat_type = u8;
	public static final int Timeformat = 47385; // u8 , mode = R/W
	public static final int Timeformat_type = u8;
	public static final int HWproduction = 47387; // u8 , mode = R/W
	public static final int HWproduction_type = u8;
	public static final int Alarmlowerroomtemp = 47388; // u8 , mode = R/W
	public static final int Alarmlowerroomtemp_type = u8;
	public static final int AlarmlowerHWtemp = 47389; // u8 , mode = R/W
	public static final int AlarmlowerHWtemp_type = u8;
	public static final int UseroomsensorS4 = 47391; // u8 , mode = R/W
	public static final int UseroomsensorS4_type = u8;
	public static final int UseroomsensorS3 = 47392; // u8 , mode = R/W
	public static final int UseroomsensorS3_type = u8;
	public static final int UseroomsensorS2 = 47393; // u8 , mode = R/W
	public static final int UseroomsensorS2_type = u8;
	public static final int UseroomsensorS1 = 47394; // u8 , mode = R/W
	public static final int UseroomsensorS1_type = u8;
	public static final int RoomsensorsetpointS4 = 47395; // s16 , mode = R/W
	public static final int RoomsensorsetpointS4_type = s16;
	public static final int RoomsensorsetpointS3 = 47396; // s16 , mode = R/W
	public static final int RoomsensorsetpointS3_type = s16;
	public static final int RoomsensorsetpointS2 = 47397; // s16 , mode = R/W
	public static final int RoomsensorsetpointS2_type = s16;
	public static final int RoomsensorsetpointS1 = 47398; // s16 , mode = R/W
	public static final int RoomsensorsetpointS1_type = s16;
	public static final int RoomsensorfactorS4 = 47399; // u8 , mode = R/W
	public static final int RoomsensorfactorS4_type = u8;
	public static final int RoomsensorfactorS3 = 47400; // u8 , mode = R/W
	public static final int RoomsensorfactorS3_type = u8;
	public static final int RoomsensorfactorS2 = 47401; // u8 , mode = R/W
	public static final int RoomsensorfactorS2_type = u8;
	public static final int RoomsensorfactorS1 = 47402; // u8 , mode = R/W
	public static final int RoomsensorfactorS1_type = u8;
	public static final int presetflowclimsys = 47442; // u8 , mode = R/W
	public static final int presetflowclimsys_type = u8;
	public static final int Operationalmode = 47570; // u8 , mode = R/W
	public static final int Operationalmode_type = u8;
	public static final int DMstartextadd = 47629; // s16 , mode = R/W
	public static final int DMstartextadd_type = s16;
	public static final int Externaladdstepcontrolled = 47631; // u8 , mode =
																// R/W
	public static final int Externaladdstepcontrolled_type = u8;
	public static final int Externaladdminruntime = 47632; // u8 , mode = R/W
	public static final int Externaladdminruntime_type = u8;
	public static final int SetpointforBT74 = 48074; // s16 , mode = R/W
	public static final int SetpointforBT74_type = s16;
	public static final int Heatmediumpumpmanualspeed = 48085; // s8 , mode =
																// R/W
	public static final int Heatmediumpumpmanualspeed_type = s8;
	public static final int Pool1accessory = 48088; // u8 , mode = R/W
	public static final int Pool1accessory_type = u8;
	public static final int Pool1starttemp = 48090; // s16 , mode = R/W
	public static final int Pool1starttemp_type = s16;
	public static final int Pool1stoptemp = 48092; // s16 , mode = R/W
	public static final int Pool1stoptemp_type = s16;
	public static final int Pool1Activated = 48094; // u8 , mode = R/W
	public static final int Pool1Activated_type = u8;
	public static final int Externaladdaccessory = 48099; // u8 , mode = R/W
	public static final int Externaladdaccessory_type = u8;
	public static final int Chargepumpmanualspeed = 48107; // s8 , mode = R/W
	public static final int Chargepumpmanualspeed_type = s8;
	public static final int Manualheatmediumpumpspeed = 48130; // s8 , mode =
																// R/W
	public static final int Manualheatmediumpumpspeed_type = s8;
	public static final int Manualchargepumpspeed = 48131; // s8 , mode = R/W
	public static final int Manualchargepumpspeed_type = s8;
	public static final int Operationalmodechargepump = 48134; // u8 , mode =
																// R/W
	public static final int Operationalmodechargepump_type = u8;
	public static final int Externalcoolingaccessory = 48156; // u8 , mode = R/W
	public static final int Externalcoolingaccessory_type = u8;
	public static final int MincoolingsupplytempS4 = 48174; // s8 , mode = R/W
	public static final int MincoolingsupplytempS4_type = s8;
	public static final int MincoolingsupplytempS3 = 48175; // s8 , mode = R/W
	public static final int MincoolingsupplytempS3_type = s8;
	public static final int MincoolingsupplytempS2 = 48176; // s8 , mode = R/W
	public static final int MincoolingsupplytempS2_type = s8;
	public static final int MincoolingsupplytempS1 = 48177; // s8 , mode = R/W
	public static final int MincoolingsupplytempS1_type = s8;
	public static final int Coolingsupplytempat20C0 = 48178; // s8 , mode = R/W
	public static final int Coolingsupplytempat20C_type0 = s8;
	public static final int Coolingsupplytempat20C1 = 48179; // s8 , mode = R/W
	public static final int Coolingsupplytempat20C_type1 = s8;
	public static final int Coolingsupplytempat20C2 = 48180; // s8 , mode = R/W
	public static final int Coolingsupplytempat20C_type2 = s8;
	public static final int Coolingsupplytempat20C3 = 48181; // s8 , mode = R/W
	public static final int Coolingsupplytempat20C_type3 = s8;
	public static final int Coolingsupplytempat40C0 = 48182; // s8 , mode = R/W
	public static final int Coolingsupplytempat40C_type0 = s8;
	public static final int Coolingsupplytempat40C1 = 48183; // s8 , mode = R/W
	public static final int Coolingsupplytempat40C_type1 = s8;
	public static final int Coolingsupplytempat40C2 = 48184; // s8 , mode = R/W
	public static final int Coolingsupplytempat40C_type2 = s8;
	public static final int Coolingsupplytempat40C3 = 48185; // s8 , mode = R/W
	public static final int Coolingsupplytempat40C_type3 = s8;
	public static final int PeriodCool = 48207; // u8 , mode = R/W
	public static final int PeriodCool_type = u8;
	public static final int Operationalmodecoolpump = 48208; // u8 , mode = R/W
	public static final int Operationalmodecoolpump_type = u8;
	public static final int Coolingdeltatempat20C = 48214; // s8 , mode = R/W
	public static final int Coolingdeltatempat20C_type = s8;
	public static final int Coolingdeltatempat40C = 48215; // s8 , mode = R/W
	public static final int Coolingdeltatempat40C_type = s8;
	
	//Creating array for each parameter including the register and factor
	// For adding parameters to read from HP, you need to modify here 
		public static final int[] HeatCurveOffsetS1 ={OffsetS1 , 1};
		public static final int[] OutdoorTempBT1 ={BT1Outdoortemp , 10};
		public static final int[] CompressorStatusEP14 ={CompressorstatusEP14 , 1};
		public static final int[] HotWatermode ={Hotwatermode , 1};
		public static final int[] MinSupplySysS1 ={MinSupplySystem1 , 10};
		public static final int[] CalculatedSupplyTempS1 ={CalculatedSupplyTemperatureS1 , 10};
		public static final int[] SupplyTempBT2S1 ={BT2SupplytempS1 , 10};
		public static final int[] DesgreeMinutes ={DegreeMinutes , 10};
		public static final int[] HeatCurveS1 ={HeatcurveS1 , 1};
		public static final int[] AdditivePower ={InteladdPower , 100};
		public static final int[] OperationalMode ={Operationalmode , 1};
		public static final int[] CompressorStartCount ={CompressorstartsEB100EP14 , 1};
		public static final int[] CompressorState ={CompressorStateEP14 , 1};
		public static final int[] BoilerTemp ={EM1BT52Boilertemperature , 10};
		public static final int[] AllowAdditiveHeat ={AllowAdditiveHeating , 1};
		public static final int[] AllowHeat ={AllowHeating , 1}; //in neils file it is: 43371
		public static final int[] IndoorRoomTempS1 = {BT50RoomTempS1 , 10};

		
		public  HashMap<String, int[]> commands = new HashMap<String, int[]>();
		
		public void makeMap(){
			commands.put("offsetS1", HeatCurveOffsetS1);
			commands.put("outdoortemp", OutdoorTempBT1);
			commands.put("indoortemp", IndoorRoomTempS1);
			commands.put("compressorstatus", CompressorStatusEP14);
			commands.put("hotwatermode", HotWatermode);
			commands.put("minsupply", MinSupplySysS1);
			commands.put("calculatedsupply", CalculatedSupplyTempS1);
			commands.put("supplytemp", SupplyTempBT2S1);
			commands.put("degreeminutes", DesgreeMinutes);
			commands.put("heatcurve", HeatCurveS1);
			commands.put("additivepower", AdditivePower);
			commands.put("operationalmode", OperationalMode);
			//commands.put("compressorstartcount", CompressorStartCount);
			//commands.put("compressorstate", CompressorState);
			//commands.put("boilertemp", BoilerTemp);
			//commands.put("allowadditiveheat", AllowAdditiveHeat);
			//commands.put("allowheat", AllowHeat);
			
		}
}
