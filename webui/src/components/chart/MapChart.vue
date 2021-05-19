<template>
  <div :id='id'></div>
</template>

<script>
import * as echarts from 'echarts'
import map_json from './json/map.json'

export default {
  name: "MapChart",
  props: {
    id: {
      type: String
    },
    MapData: {
      type: Array,
      default: () => [
        {name: "合肥", value: 67},
        {name: "武汉", value: 58},
        {name: "南京", value: 128},
        {name: "上海", value: 24}
      ]
    }
  },
  data() {
    return {
      idT: ''
    }
  },
  watch: {
    'id': {
      handler(newVal, oldVal) {
        this.idT = newVal
      },
      deep: true
    },
    'MapData': {
      handler(newVal) {
        this.MapData = newVal
        this.renderChart()
      },
      deep: true
    }
  },
  mounted() {
    let that = this
    setTimeout(function () {
      that.renderChart()
    }, 600)
  },
  methods: {
    renderChart() {
      let uploadedDataURL = "/json/map.json"
      let myChart = echarts.init(document.getElementById(this.id))
      let geoJson = map_json
      echarts.registerMap('china', geoJson);
      myChart.hideLoading();
      let geoCoordMap = {
        '台湾': [121.5135, 25.0308],
        '黑龙江': [127.9688, 45.368],
        '内蒙古': [110.3467, 41.4899],
        "吉林": [125.8154, 44.2584],
        '北京市': [116.4551, 40.2539],
        "辽宁": [123.1238, 42.1216],
        "河北": [114.4995, 38.1006],
        "天津": [117.4219, 39.4189],
        "山西": [112.3352, 37.9413],
        "陕西": [109.1162, 34.2004],
        "甘肃": [103.5901, 36.3043],
        "宁夏": [106.3586, 38.1775],
        "青海": [101.4038, 36.8207],
        "新疆": [87.9236, 43.5883],
        "西藏": [91.11, 29.97],
        "四川": [103.9526, 30.7617],
        "重庆": [108.384366, 30.439702],
        "山东": [117.1582, 36.8701],
        "河南": [113.4668, 34.6234],
        "南京": [118.8062, 31.9208],
        "合肥": [117.29, 32.0581],
        "武汉": [114.3896, 30.6628],
        "浙江": [119.5313, 29.8773],
        "福建": [119.4543, 25.9222],
        "江西": [116.0046, 28.6633],
        "湖南": [113.0823, 28.2568],
        "贵州": [106.6992, 26.7682],
        "云南": [102.9199, 25.4663],
        "广东": [113.12244, 23.009505],
        "广西": [108.479, 23.1152],
        "海南": [110.3893, 19.8516],
        '上海': [121.4648, 31.2891]
      };
      let max = 480, min = 9; // todo
      let maxSize4Pin = 100, minSize4Pin = 20;

      let convertData = function (data) {
        let res = [];
        for (var i = 0; i < data.length; i++) {
          let geoCoord = geoCoordMap[data[i].name];
          if (geoCoord) {
            res.push({
              name: data[i].name,
              value: geoCoord.concat(data[i].value)
            });
          }
        }
        return res;
      };

      let option = {
        title: {
          top: 20,
          text: '集团工厂分布',
          subtext: '',
          x: 'center',
          textStyle: {
            color: '#ccc'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: function (params) {
            if (typeof (params.value)[2] == "undefined") {
              return params.name + ' : ' + (isNaN(params.value) ? 0 : params.value);
            } else {
              return params.name + ' : ' + params.value[2] || 0;
            }
          }
        },
        legend: {
          orient: 'vertical',
          y: 'bottom',
          x: 'right',
          data: ['稼动率'],
          textStyle: {
            color: '#fff'
          }
        },
        visualMap: {
          show: false,
          min: 0,
          max: 500,
          left: 'left',
          top: 'bottom',
          text: ['高', '低'], // 文本，默认为数值文本
          calculable: true,
          seriesIndex: [1],
          inRange: {}
        },
        geo: {
          map: 'china',
          show: true,
          roam: true,
          label: {
            normal: {
              show: false
            },
            emphasis: {
              show: false,
            }
          },
          itemStyle: {
            normal: {
              areaColor: '#588ad2',
              borderColor: '#0a53e9',//线
              shadowColor: '#092f8f',//外发光
              shadowBlur: 20
            },
            emphasis: {
              areaColor: '#0a2dae',//悬浮区背景
            }
          }
        },
        series: [
          {
            symbolSize: 5,
            label: {
              normal: {
                formatter: '{b}',
                position: 'right',
                show: true
              },
              emphasis: {
                show: true
              }
            },
            itemStyle: {
              normal: {
                color: '#fff'
              }
            },
            name: 'light',
            type: 'scatter',
            coordinateSystem: 'geo',
            data: convertData(this.MapData),

          },
          {
            type: 'map',
            map: 'china',
            geoIndex: 0,
            aspectScale: 1, //长宽比
            showLegendSymbol: true, // 存在legend时显示
            label: {
              normal: {
                show: true
              },
              emphasis: {
                show: true,
                textStyle: {
                  color: '#fff'
                }
              }
            },
            roam: true,
            itemStyle: {
              normal: {
                areaColor: '#031525',
                borderColor: '#FFFFFF',
              },
              emphasis: {
                areaColor: '#2B91B7'
              }
            },
            animation: false,
            data: this.MapData
          },
          {
            name: 'Top 5',
            type: 'scatter',
            coordinateSystem: 'geo',
            symbol: 'pin',
            symbolSize: [50, 50],
            label: {
              normal: {
                show: true,
                textStyle: {
                  color: '#fff',
                  fontSize: 9,
                },
                formatter(value) {
                  return value.data.value[2]
                }
              }
            },
            itemStyle: {
              normal: {
                color: '#d84f37', //标志颜色
              }
            },
            data: convertData(this.MapData),
            showEffectOn: 'render',
            rippleEffect: {
              brushType: 'stroke'
            },
            hoverAnimation: true,
            zlevel: 1
          },

        ]
      };
      myChart.setOption(option);
      window.addEventListener('resize', function () {
        if (!myChart.isDisposed()) {
          myChart.resize()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>