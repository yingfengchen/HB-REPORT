<template>
  <div :id="idT"></div>
</template>

<script>
import * as echarts from 'echarts'
import { randomUUID } from '@/utils/util'

export default {
  name: 'NoAreaLineChart',
  props: {
    id: {
      type: String,
      default: randomUUID()
    },
    showSplitLine: {
      type: Boolean,
      default: true
    },
    seriesData: {
      type: Array,
      default: () => [{ name: '设备1', data: [13, 10, 3, 12, 15, 30, 7] }]
    },
    xAxis: {
      type: Array,
      default: () => ['设备1', '设备2', '设备3', '设备4'],
      axisLabel: {
        margin: 20,
        color: '#727272'
      },
      axisLine: {
        lineStyle: {
          color: 'rgb(71,71,71)'
        }
      },
      axisTick: {
        show: false
      }
    },
    yAxis: {
      type: Array,
      default: () => [{
        name: '单位：m³',
        type: 'value',
        axisTick: {
          show: false
        },
        axisLine: {
          lineStyle: {
            color: '#989898'
          }
        },
        axisLabel: {
          textStyle: {
            color: '#bcbcbc'
          }
        },
        splitLine: {
          show: false
        }
      }]
    },
    boundaryGap: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      seriesDataT: [],
      idT: ''
    }
  },
  watch: {
    'seriesData': {
      handler(newVal, oldVal) {
        this.seriesDataT = newVal
        this.renderChart()
      },
      deep: true
    },
    'id': {
      handler(newVal, oldVal) {
        this.idT = newVal
      },
      deep: true
    }
  },
  mounted() {
    this.seriesDataT = this.seriesData
    this.idT = this.id
    this.renderChart()
  },
  methods: {
    renderChart() {
      let that = this
      setTimeout(function() {
        that.initLineChart(that.id, that.xAxis, that.seriesData, that.showSplitLine, that.yAxis)
      }, 600)
    },
    initLineChart(id, x_axis, series, showSplitLine) {
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
            type: s['type'] || 'line',
            data: s['data'],
            symbolSize: 6,
            barMaxWidth: 30,
            stack: s['stack'] || null,
            symbol: 'circle',
            smooth: true,
            yAxisIndex: s['yIndex'] || 0,
            lineStyle: {
              color: color[i]
            },
            itemStyle: {
              normal: {
                color: color[i],
                borderColor: color[i]
              }
            },
            emphasis: {
              itemStyle: {
                color: {
                  type: 'radial',
                  x: 0.5,
                  y: 0.5,
                  r: 0.5,
                  colorStops: [{
                    offset: 0,
                    color: '#fe9a8b'
                  },
                    {
                      offset: 0.4,
                      color: '#fe9a8b'
                    },
                    {
                      offset: 0.5,
                      color: '#fff'
                    }, {
                      offset: 0.7,
                      color: '#fff'
                    }, {
                      offset: 0.8,
                      color: '#fff'
                    }, {
                      offset: 1,
                      color: '#fff'
                    }
                  ]
                },
                borderColor: '#fe9a8b',
                borderWidth: 2
              }
            }
          }
          series_value.push(ser)
        }

        const optionData = {
          legend: {
            type: 'scroll',
            show: true,
            top: '1%',
            left: '20',
            width: '70%',
            textStyle: {
              color: '#bcbcbc'
            }
          },
          tooltip: {
            trigger: 'axis'
          },
          toolbox: {
            feature: {
              magicType: { type: ['line', 'bar'] },
              saveAsImage: {}
            }
          },
          grid: {
            left: '60px',
            right: '45px',
            top: '70px',
            bottom: '20%'
          },
          xAxis: [{
            type: 'category',
            data: x_axis,
            axisLine: {
              lineStyle: {
                color: '#8d8d8d'
              }
            },
            axisTick: {
              show: true
            },
            axisLabel: {
              textStyle: {
                color: '#bcbcbc'
              },
              margin: 15
            },
            boundaryGap: this.boundaryGap
          }],
          yAxis: this.yAxis,
          series: series_value
        }

        this.chart.setOption(optionData)
        window.addEventListener('resize', function() {
          if (!that.chart.isDisposed()) {
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