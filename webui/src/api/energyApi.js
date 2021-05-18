import { getAction, deleteAction, putAction, postAction, httpAction } from '@/api/manage'

//获取累计值趋势
const getCumulative = (params) => postAction('/energy/getCumulativeTrend', params)
//获取瞬时值趋势
const getInstantaneous = (params) => postAction('/energy/getInstantaneousValueTrend', params)
//查询相应节点当天当月的瞬时值
const getDayMonthValueOfTags = (params) => postAction('/energy/getDayMonthValueOfTags', params)
//查询相应节点当天当月的累计差值
const getDayMonthDiffValueOfTags = (params) => postAction('/energy/getDayMonthDiffValueOfTags', params)
//查询相应节点24小时内的趋势
const getTrendOfOneTag24Hour = (params) => postAction('/energy/getTrendOfOneTag24Hour', params)
//查询一个节点近两天的数据
const getTwoDayDiffRateOfOnTag = (params) => postAction('/energy/getTwoDayDiffRateOfOnTag', params)
//查询多个节点在某段时间内的消耗量
const getRangeEnergyConsume = (params) => postAction('/energy/getRangeEnergyConsume', params)

export {
  getCumulative,
  getInstantaneous,
  getDayMonthValueOfTags,
  getDayMonthDiffValueOfTags,
  getTrendOfOneTag24Hour,
  getTwoDayDiffRateOfOnTag,
  getRangeEnergyConsume
}