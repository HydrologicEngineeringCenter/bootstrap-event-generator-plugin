<?xml version="1.0" encoding="UTF-8"?>
<HydrologicSampling Name="HSBS_Continuous" DataOK="true" Type="Hydrograph" IsMultipleDuration="false">
  <Version>2.0</Version>
  <Description>
    <desc cnt="0" value="Copy of HSBS" />
  </Description>
  <HydroRate Modality="Annual" />
  <HydroSeasons>
    <HydroSeason SeasonShape="Uniform" Start="01oct" End="30Sep" />
    <HydroSeason SeasonShape="Uniform" Start="01Jan" End="31Dec" />
  </HydroSeasons>
  <SampledSeason>Both</SampledSeason>
  <NumberSeasons>One</NumberSeasons>
  <SeasonOneName />
  <SeasonTwoName />
  <MinTimeBetweenEvents>0.0</MinTimeBetweenEvents>
  <ProbabilitySeasonOne>1.0</ProbabilitySeasonOne>
  <ProbabilitySeasonTwo>0.0</ProbabilitySeasonTwo>
  <SeasonHydroEvents>
    <SeasonHydroEvent>
      <SeasonName />
      <HydroEvents Method="Historic" HasForecast="false" UseIPOverride="false" ContSim="true">
        <IncrProbOverride VerifiedIPOverride="false">
          <IncrProbTable />
        </IncrProbOverride>
        <HydrographSettings UncertMethod="User_Defined" UncertUserYOR="30">
          <HistoricRankingInfo RankingLocation="CCP1" RankingParameter="Flow" RankingDuration="45" />
          <ExceedanceProbs />
        </HydrographSettings>
        <ForecastSettings>
          <ForecastLocTable />
          <HstCorrMatrix />
        </ForecastSettings>
        <HydrographHistoricRecord>
          <location filename="shared/flows.dss" location="CCP1" parameter="Flow" cpart="Flow" pathname="/MAIN RIVER/MAIN AT FORK DAM/FLOW//1DAY/USGS/" isactive="true" />
          <location filename="" location="CCP2" parameter="Flow" cpart="Flow" pathname="///////" isactive="false" />
        </HydrographHistoricRecord>
        <ForecastHistoricRecord />
        <SyntheticYears />
        <SerialCorrelationData UseSerialCorrelation="true" ApplyCorrelationToSubSample="true" SerialCorrelationValue="0.15" RankingStartDate="01JAN" RankingEndDate="31MAY" />
      </HydroEvents>
    </SeasonHydroEvent>
    <SeasonHydroEvent>
      <SeasonName />
      <HydroEvents Method="Historic" HasForecast="false" UseIPOverride="false" ContSim="false">
        <IncrProbOverride VerifiedIPOverride="false">
          <IncrProbTable />
        </IncrProbOverride>
        <HydrographSettings UncertMethod="None">
          <HistoricRankingInfo RankingLocation="CCP1" RankingParameter="Flow" RankingDuration="45" />
          <ExceedanceProbs />
        </HydrographSettings>
        <ForecastSettings>
          <ForecastLocTable />
          <HstCorrMatrix />
        </ForecastSettings>
        <HydrographHistoricRecord>
          <location filename="shared/flows.dss" location="CCP1" parameter="Flow" cpart="Flow" pathname="/TRIBUTARY/TRIBUTARY ABV FORK/FLOW//1DAY/USGS/" isactive="true" />
        </HydrographHistoricRecord>
        <ForecastHistoricRecord />
        <SyntheticYears />
        <SerialCorrelationData UseSerialCorrelation="false" ApplyCorrelationToSubSample="true" SerialCorrelationValue="0.0" RankingStartDate="01Oct" RankingEndDate="30Sep" />
      </HydroEvents>
    </SeasonHydroEvent>
  </SeasonHydroEvents>
  <DataLocations />
  <StratificationSettings StratificationDist="GUMBEL" StratificationLocTag="CCP1" StratificationFirstBound="1.0E-7" ApplyStratification="false" />
</HydrologicSampling>

