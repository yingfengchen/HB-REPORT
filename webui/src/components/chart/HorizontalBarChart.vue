<template>
  <div :id='id'></div>
</template>

<script>
import * as echarts from 'echarts'
export default {
  name: 'HorizontalBarChart',
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
      let legend_default = ['设备1', '设备2', '设备3', '设备4', '设备5', '设备6', '设备7', '设备8']
      let series_default = [
        {name: 'RUN', data: [13, 10, 3, 12, 15, 8, 7, 9], yIndex: 0, stack: 'state'},
        {name: 'IDLE', data: [5, 12, 8, 12, 6, 21, 7, 9], yIndex: 0, stack: 'state'},
        {name: 'PM', data: [5, 12, 8, 12, 6, 21, 7, 9], yIndex: 0, stack: 'state'},
        {name: 'TROUBLE', data: [5, 12, 8, 12, 6, 21, 7, 9], yIndex: 0, stack: 'state'}
      ]
      let yAxisDefault = [{
        name: '片数',
        axisLabel: {
          formatter: '{value}',
          color: '#bcbcbc'
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
        const color = ['#60cfa8', '#4f9fd4', '#2254d2', '#925bf7', '#FCC248', '#FCE448', '#F58B41', '#F7765B', '#525ECD', '#547FDB']
        const series_value = []

        for (let i = 0; i < series.length; i++) {
          const s = series[i]
          const ser = {
            name: s['name'],
            type: 'bar',
            data: s['data'],
            stack: s['stack'] || null,
            barMaxWidth: 20,
            yAxisIndex: s['yIndex'] || 0,
            lineStyle: {
              color: color[i]
            },
            itemStyle: {
              normal: {
                color: color[i],
                borderColor: color[i]
              }
            }
          }
          series_value.push(ser)
        }

        const optionData = {
          legend: {
            show: true,
            top: '10px',
            left: '20px',
            textStyle: {
              color: '#bcbcbc'
            }
          },
          tooltip: {
            trigger: 'axis'
          },
          toolbox: {
            feature: {
              dataView: {readOnly: false},
              magicType: {type: ['line', 'bar']},
              saveAsImage: {}
            }
          },
          grid: {
            top: '60px',
            right: '62px',
            left: '72px',
            bottom: '40px'
          },
          yAxis: [{
            type: 'category',
            color: '#59588D',
            data: x_axis,
            axisLabel: {
              margin: 20,
              color: '#bcbcbc'
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
          xAxis: yAxis,
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