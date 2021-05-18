<template>
  <div style='' :id='id'></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'GaugeChart',
  props: ['id', 'value'],
  data() {
    return {
      chart: null,
      value_real: 0
    }
  },
  watch: {
    'value': function(newVal) {
      this.value_real = newVal
      this.initData()
    }
  },
  mounted() {
    this.initData()
  },
  methods: {
    initData() {
      let that = this
      this.value_real = this.value
      setTimeout(function() {
        that.initGaugeChart(that.id, that.value_real)
      }, 600)
    },
    initGaugeChart(id, value) {
      let that = this
      if (document.getElementById(id)) {
        echarts.dispose(document.getElementById(id))
        this.chart = echarts.init(document.getElementById(id))
        this.chart.clear()

        let color = new echarts.graphic.LinearGradient(
          0, 0, 1, 0,
          [{
            offset: 0,
            color: '#58D9F9'
          },
            {
              offset: 1,
              color: '#4992FF'
            }
          ]
        )
        const optionData = {
          series: [{
            type: 'gauge',
            startAngle: 220,
            endAngle: -40,
            min: 0,
            max: 1000,
            radius: '90%',
            title: {
              show: false
            },
            detail: {
              backgroundColor: '#fff',
              borderColor: '#999',
              borderWidth: 2,
              width: '50%',
              borderRadius: 4,
              offsetCenter: [0, '70%'],
              formatter: function(value) {
                return '{value|' + value.toFixed(0) + '}{unit|kWh}'
              },
              rich: {
                value: {
                  fontSize: 14,
                  fontWeight: 'bolder',
                  color: '#777'
                },
                unit: {
                  fontSize: 14,
                  color: '#999',
                  padding: [0, 0, 0, 6]
                }
              }
            },
            axisLine: {
              show: false
            },
            axisTick: {
              length: 10,
              lineStyle: {
                color: '#999'
              }
            },
            splitLine: {
              length: 15,
              lineStyle: {
                color: '#999',
                width: 3
              }
            },
            axisLabel: {
              color: '#999'
            },
            pointer: {
              width: 10,
              length: '50%'
            },
            itemStyle: {
              color: color,
              shadowColor: 'rgba(0,138,255,0.45)',
              shadowBlur: 10,
              shadowOffsetX: 2,
              shadowOffsetY: 2
            },
            markPoint: {
              data: [{
                x: '50%',
                y: '50%',
                symbol: 'circle',
                symbolSize: 8,
                itemStyle: {
                  color: '#fff'
                }
              }]
            },
            data: [{
              value: value
            }]
          },
            {
              type: 'gauge',
              radius: '93%',
              startAngle: 220,
              endAngle: -40,
              min: 0,
              max: 1000,
              title: {
                show: false
              },
              detail: {
                show: false
              },
              axisLine: {
                show: true,
                lineStyle: {
                  width: 16,
                  color: [
                    [
                      value / 1000, color
                    ],
                    [
                      1, 'rgba(225,225,225,0.4)'
                    ]
                  ]
                }
              },
              axisTick: {
                show: false
              },
              splitLine: {
                show: false
              },
              axisLabel: {
                show: false
              },
              pointer: {
                show: false
              },
              itemStyle: {
                normal: {
                  color: '#54F200'
                }
              }
            }
          ]
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
.main-div {
  height: 100%;
  width: 100%;
}
</style>