<template>
  <div :id="id"></div>
</template>

<script>
import * as echarts from 'echarts'
import { randomUUID } from '@/utils/util'

const color = ['#459AF0', '#38C3B0', '#86CA5A', '#BFD44F', '#FCC248', '#FCE448', '#F58B41', '#F7765B', '#525ECD', '#897CBD', '#547FDB', '#84D6B9', '#AC78CD']

export default {
  name: 'SankeyChart',
  props: {
    id: {
      type: String,
      default: randomUUID()
    },
    nodes: {
      type: Array,
      default: () => ['一层节点1', '二层节点1', '二层节点2', '三层节点1', '三层节点2', '四层节点1', '四层节点2', '四层节点3', '四层节点4', '四层节点5', '四层节点6', '四层节点7', '四层节点8', '四层节点9', '四层节点10']
    },
    links: {
      type: Array,
      default: () => [
        {
          source: '一层节点1',
          target: '二层节点1',
          value: 10
        },
        {
          source: '一层节点1',
          target: '二层节点2',
          value: 10
        },
        {
          source: '二层节点1',
          target: '三层节点1',
          value: 5
        },
        {
          source: '二层节点1',
          target: '三层节点2',
          value: 5
        },
        {
          source: '二层节点2',
          target: '三层节点1',
          value: 5
        },
        {
          source: '二层节点2',
          target: '三层节点2',
          value: 5
        },
        {
          source: '三层节点1',
          target: '四层节点1',
          value: 2
        },
        {
          source: '三层节点1',
          target: '四层节点2',
          value: 2
        },
        {
          source: '三层节点1',
          target: '四层节点3',
          value: 2
        },
        {
          source: '三层节点1',
          target: '四层节点4',
          value: 2
        },
        {
          source: '三层节点1',
          target: '四层节点5',
          value: 2
        },
        {
          source: '三层节点2',
          target: '四层节点6',
          value: 2
        },
        {
          source: '三层节点2',
          target: '四层节点7',
          value: 2
        },
        {
          source: '三层节点2',
          target: '四层节点8',
          value: 2
        },
        {
          source: '三层节点2',
          target: '四层节点9',
          value: 2
        },
        {
          source: '三层节点2',
          target: '四层节点10',
          value: 2
        }
      ]
    }
  },
  watch: {
    links: {
      handler(newVal) {
        this.initSankeyChart()
      },
      deep: true
    }
  },
  mounted() {
    this.renderChart()
  },
  methods: {
    renderChart() {
      let that = this
      setTimeout(function() {
        that.initSankeyChart()
      }, 600)
    },
    initSankeyChart() {
      let that = this
      if (document.getElementById(this.id)) {
        echarts.dispose(document.getElementById(this.id))
        this.chart = echarts.init(document.getElementById(this.id))
        this.chart.clear()

        const optionData = {
          tooltip: {
            trigger: 'item',
            triggerOn: 'mousemove'
          },
          series: {
            type: 'sankey',
            layout: 'none',
            top: 50,
            left: '3%',
            right: '14%',
            nodeGap: 14,
            layoutIterations: 0, // 自动优化列表，尽量减少线的交叉，为0就是按照数据排列
            data: this.addColorInfo(), // 节点
            links: this.links, // 节点之间的连线
            draggable: false,
            focusNodeAdjacency: 'allEdges', // 鼠标划上时高亮的节点和连线，allEdges表示鼠标划到节点上点亮节点上的连线及连线对应的节点
            levels: [{
              depth: 0,
              itemStyle: {
                color: '#F27E7E'
              },
              lineStyle: {
                color: 'source',
                opacity: 0.2
              }
            },
              {
                depth: 1,
                lineStyle: {
                  color: 'source',
                  opacity: 0.2
                }
              },
              {
                depth: 2,
                lineStyle: {
                  color: 'source',
                  opacity: 0.2
                }
              },
              {
                depth: 3,
                label: {
                  fontSize: 12
                }
              }
            ],
            label: {
              fontSize: 14,
              color: '#666'
            },
            itemStyle: {
              normal: {
                borderWidth: 0
              }
            }
          }
        }

        this.chart.setOption(optionData)
        window.addEventListener('resize', function() {
          if (!that.chart.isDisposed()) {
            that.chart.resize()
          }
        })
      }
    },
    addColorInfo() {
      let result = []
      for (let i = 0; i < this.nodes.length; i++) {
        let n_t = this.nodes[i]
        let n_o = { name: n_t, itemStyle: { color: color[i % 13] } }
        result.push(n_o)
      }
      return result
    }
  }
}
</script>

<style scoped>

</style>