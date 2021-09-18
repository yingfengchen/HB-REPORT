<template>
  <div class="main-pie-div">
    <a-row>
      <a-col :span="24">
        <div style="height: 100%; width: 100%; display: inline-block" :id="id"></div>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { randomUUID } from '@/utils/util'

export default {
  name: 'PieChart',
  props: {
    id: {
      type: String,
      default: randomUUID()
    },
    datasource: {
      type: Array,
      default: () => [
        { value: 335, name: '低温系统耗电' },
        { value: 310, name: '中温系统耗电' },
        { value: 234, name: '热水系统耗电' }
      ]
    },
    text: {
      type: String,
      default: '总耗电量'
    },
    unit: {
      type: String,
      default: 'KWH'
    }
  },
  data() {
    return {
      totalValue: 0,
      legendList: ['低温系统耗电', '中温系统耗电', '热水系统耗电']
    }
  },
  watch: {
    'datasource': function(val) {
      this.totalValue = this.getTotalValue(val)
      this.legendList = this.getLegend(val)
      this.initPieChart(this.id, this.legendList, val)
    }
  },
  mounted() {
    this.totalValue = this.getTotalValue(this.datasource)
    this.legendList = this.getLegend(this.datasource)
    this.initPieChart(this.id, this.legendList, this.datasource)
  },
  methods: {
    initPieChart(id, legend, data) {
      let that = this
      if (document.getElementById(id)) {
        echarts.dispose(document.getElementById(id))
        this.chart = echarts.init(document.getElementById(id))
        this.chart.clear()
        const optionData = {
          color: ['#459AF0', '#38C3B0', '#86CA5A', '#BFD44F', '#FCC248', '#FCE448', '#F58B41', '#F7765B', '#525ECD', '#547FDB'],
          legend: {
            show: true,
            type: 'scroll',
            top: '3%',
            left: '12px',
            right: '12px',
            width: '70%',
            data: legend
          },
          toolbox: {
            feature: {
              saveAsImage: {}
            }
          },
          graphic: [
            {
              type: 'group',
              top: 'middle',
              left: 'center',
              id: 'data',
              children: [
                {
                  type: 'text',
                  id: 'current',
                  top: 0,
                  style: {
                    text: this.totalValue + this.unit,
                    font: 'bolder 18px "Microsoft YaHei", sans-serif',
                    fill: '#000000',
                    textAlign: 'center'
                  }
                },
                {
                  type: 'text',
                  id: 'all',
                  top: 30,
                  style: {
                    text: this.text,
                    font: 'bolder 15px "Microsoft YaHei", sans-serif',
                    fill: '#c0c0c0',
                    textAlign: 'center'
                  }
                }
              ]
            }
          ],
          grid: {
            left: '3%',
            top: '6%'
          },
          series: [
            {
              name: '轮次',
              type: 'pie',
              radius: ['40%', '70%'],
              avoidLabelOverlap: false,
              label: {
                show: false,
                fontSize: '10',
                position: 'inside',
                formatter: (params) => {
                  return `${params.name}: ${params.percent}%`
                }
              },
              emphasis: {
                label: {
                  show: true,
                  fontSize: '10',
                  fontWeight: 'bold'
                }
              },
              labelLine: {
                show: false
              },
              data: data
            }
          ]
        }

        this.chart.setOption(optionData)
        window.addEventListener('resize', function() {
          if (!that.chart.isDisposed()) {
            that.chart.resize()
          }
        })
      }
    },
    getTotalValue(data) {
      let value = 0
      data.forEach(d => {
        value += d['value']
      })
      return value
    },
    getLegend(data) {
      let legend = []
      data.forEach(d => {
        legend.push(d['name'])
      })
      return legend
    }
  }
}
</script>

<style lang="less" scoped>
.main-pie-div {
  width: 100%;
  height: 100%;

  /deep/ .ant-row {
    height: 100%;
  }

  /deep/ .ant-col {
    height: 100%;
  }
}
</style>