<template>
  <div :id='id'></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'BarChart',
  props: ['id', 'showSplitLine', 'seriesData', 'legend', 'yAxis'],
  data() {
    return {
      chart: null
    }
  },
  watch: {
    'legend': {
      handler(newVal) {
        this.initData()
      },
      deep: true
    },
    'seriesData': {
      handler(newVal) {
        this.initData()
      },
      deep: true
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      let that = this
      let legend_default = ['设备1', '设备2', '设备3', '设备4']
      let series_default = [
        {name: '产量', data: [13, 10, 3, 12, 15, 30, 7], yIndex: 0}
      ]
      let yAxisDefault = [{
        name: '片数',
        axisLabel: {
          formatter: '{value}',
          color: '#999',
          textStyle: {
            fontSize: 18
          }
        },
        axisLine: {
          lineStyle: {
            color: 'rgba(107,107,107,0.37)'
          }
        },
        axisTick: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: 'rgba(131,101,101,0.2)',
            type: 'dashed'
          }
        }
      }]
      setTimeout(function() {
        that.initBarChart(that.id, that.legend || legend_default, that.seriesData || series_default, that.showSplitLine, that.yAxis || yAxisDefault)
      }, 600)
    },
    initBarChart(id, x_axis, series, showSplitLine, yAxis) {
      let that = this
      if (document.getElementById(id)) {
        echarts.dispose(document.getElementById(id))
        this.chart = echarts.init(document.getElementById(id))
        this.chart.clear()
        const color = ['#459AF0', '#38C3B0', '#86CA5A', '#BFD44F', '#FCC248', '#FCE448', '#F58B41', '#F7765B', '#525ECD', '#547FDB']
        const series_value = []

        for (let i = 0; i < series.length; i++) {
          const s = series[i]
          const ser = {
            name: s['name'],
            type: 'bar',
            data: s['data'],
            barMaxWidth: 30,
            yAxisIndex: s['yIndex'] || 0,
            lineStyle: {
              color: color[i]
            },
            itemStyle: {
              normal: {
                color: color[i],
                borderColor: color[i],
                barBorderRadius: [30, 30, 0, 0]
              }
            }
          }
          series_value.push(ser)
        }

        const optionData = {
          legend: {
            show: true,
            top: '1%',
            left: '20'
          },
          tooltip: {
            trigger: 'axis'
          },
          toolbox: {
            feature: {
              magicType: {type: ['line', 'bar']},
              saveAsImage: {}
            }
          },
          grid: {
            top: '80px',
            right: '22px',
            left: '62px',
            bottom: '60px'
          },
          xAxis: [{
            type: 'category',
            color: '#59588D',
            data: x_axis,
            axisLabel: {
              margin: 20,
              color: '#727272'
            },
            axisLine: {
              lineStyle: {
                color: 'rgba(107,107,107,0.37)'
              }
            },
            axisTick: {
              show: false
            }
          }],
          yAxis: yAxis,
          series: series_value
        }

        this.chart.setOption(optionData)
        window.addEventListener('resize', function() {
          if(!that.chart.isDisposed()) {
            that.chart.resize()
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>