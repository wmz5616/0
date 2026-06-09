// import React from 'react';
// import { UserOutlined, SearchOutlined } from '@ant-design/icons';
// import {
//   Form,
//   Input,
//   Button,
//   Row,
//   Col,
//   Spin,
//   message,
//   Popconfirm,
//   Table,
//   Select,
//   DatePicker,
//   Tabs,
//   Checkbox,
//   Upload,
//   Modal,
// } from 'antd';
// import { history, connect, Link } from 'umi';
// import QQMap from 'qqmap';
// // import { thisExpression } from '@babel/types';
// // import { setToken } from '@/utils/authority';

// const { Option } = Select;
// const { RangePicker } = DatePicker;
// // 交通信息
// let key = 'F2OBZ-ZGK63-6MO3A-3TFAY-PG4IS-3NBY5';
// class Login extends React.Component {
//   formRef = React.createRef();
//   state = {
//     spinning: false,
//     pageNum: 1,
//     list: [],
//     fileList: [],
//   };

//   componentDidMount() {
//     this.initMap();
//   }

//   handleOk = () => {
//     const { handleCancel, addressDetails } = this.props;
//     addressDetails(this.state.address, this.state.lat, this.state.lng);
//     handleCancel();
//   };

//   handleCancel = () => {
//     const { handleCancel } = this.props;
//     handleCancel();
//   };

//   TMap = (key) => {
//     return new Promise(function (resolve, reject) {
//       window.init = function () {
//         resolve(qq);
//       };
//       var script = document.createElement('script');
//       script.type = 'text/javascript';
//       script.src = 'https://map.qq.com/api/js?v=2.exp&callback=init&key=' + key+'&libraries=place';

//       script.onerror = reject;
//       document.head.appendChild(script);
//     });
//   };

//   initMap = () => {
//     //设置中心坐标
//     this.TMap(key).then((qq) => {
//       var map = new qq.maps.Map(document.getElementById('container'), {
//         center: new qq.maps.LatLng(23.021016, 113.751884),
//         zoom: 20,
//         city1: '东莞市', //城市
//       });
//       //实例化自动完成
//       var ap = new qq.maps.place.Autocomplete(document.getElementById('place'));

//       let markerlast = new qq.maps.Marker({
//         position: map.center,
//         map: map,
//       });

//       var keyword = '';
//       let markers = []; //用户搜索后显示的点的集合


      
//       //调用Poi检索类。用于进行本地检索、周边检索等服务。
//       var searchService = new qq.maps.SearchService({
//         complete: (results) => {
//           if (results.type === 'CITY_LIST') {
//             searchService.setLocation(results.detail.cities[0].cityName);
//             searchService.search(keyword);
//             return;
//           }
//           var pois = results.detail.pois;
//           console.log(pois[0].name);
//           this.setState({
//             address: pois[0].address,
//             lat: pois[0].latLng.lat,
//             lng: pois[0].latLng.lng,
//             name:pois[0].name
//           });

//           var latlngBounds = new qq.maps.LatLngBounds();
//           for (var i = 0, l = 1; i < l; i++) {
//             var poi = pois[i];
//             // console.log(poi);
//             latlngBounds.extend(poi.latLng);
//             var marker = new qq.maps.Marker({
//               map: map,
//               position: poi.latLng,
//               address: poi.latLng.address,
//             });

//             marker.setTitle(poi.name);
//             markers.push(marker);
//           }
//           map.fitBounds(latlngBounds);
//         },
//       });

//       this.markers = markers;

//       //添加监听事件
//       console.log(qq.maps.event)
//       qq.maps.event.addListener(ap, 'confirm', function (res) {
//         console.log(111);
//         keyword = res.value;
//         searchService.search(keyword);
//       });

//       qq.maps.event.addListener(map, 'click', (event) => {
//         // 清除初始化位置

//         markerlast.position = event.latLng;
//         markerlast.setMap(null);
//         // 获取经纬度位置

//         // 绘制点击的点
//         let marker = new qq.maps.Marker({
//           position: event.latLng,
//           map: map,
//         });

//         console.log(marker);
//         this.setState({
//           lat: marker.position.lat,
//           lng: marker.position.lng,
//         });
//         // 添加监听事件   获取鼠标单击事件
//         qq.maps.event.addListener(map, 'click', function (event) {
//           marker.setMap(null);
//         });
//         // 清空上一次搜索结果
//         Array.from(this.markers).forEach((marker) => {
//           marker.setMap(null);
//         });
//       });
//     });
//   };

//   handleAdress = (v) => {
//     console.log(v.target.value);
//     this.setState({
//       ccc: v.target.value,
//     });
//   };
//   render() {
//     return (
//       <div>
//         <Modal title="场所定位" visible onOk={this.handleOk} onCancel={this.handleCancel} width={800}>
//           <div>
//             <Input type="text" id="place" placeholder="请输入地点名称" value={this.state.name} onChange={(e)=>{this.setState({name:e.target.value})}} suffix={<SearchOutlined  style={{color:'#ccc'}} />}/>
//             <div id="container" style={{ height: 500, marginTop: 15 }} />
//           </div>
//         </Modal>
//       </div>
//     );
//   }
// }

// export default connect()(Login);
import React from 'react';
import { UserOutlined, SearchOutlined } from '@ant-design/icons';
import { TextField, Paper, MenuItem, Popper } from '@material-ui/core';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  DatePicker,
  Tabs,
  Checkbox,
  Upload,
  Modal,
} from 'antd';
import { history, connect, Link } from 'umi';
import { throttleSetter } from 'lodash-decorators';
// import { thisExpression } from '@babel/types';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
// 交通信息
let value = '';
let key = '7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH';
class Maps extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    fileList: [],
    info: '',
    lat: 23.021016,
    lng: 113.751884,
    asd: false,
    searchList: [],
    anchorEl: null,
  };

  componentDidMount() {
    this.initMap();
  }

  handleOk = () => {
    const { handleCancel, addressDetails } = this.props;
    addressDetails(this.state.address, this.state.lat, this.state.lng);
    handleCancel();
  };

  handleCancel = () => {
    const { handleCancel } = this.props;
    handleCancel();
  };


  TMap = (key) => {
    return new Promise(function (resolve, reject) {
      window.init = function () {
        resolve(qq);
      };
      var script = document.createElement('script');
      script.type = 'text/javascript';
      script.src =
        'https://map.qq.com/api/js?v=2.exp&callback=init&key=' + key + '&libraries=place';

      script.onerror = reject;
      document.head.appendChild(script);
    });
  };

  initMap = () => {
    //设置中心坐标
    this.TMap(key).then((qq) => {
      var map = new qq.maps.Map(document.getElementById('container'), {
        center: new qq.maps.LatLng(this.state.lat, this.state.lng),
        zoom: 15,
        city1: '东莞市', //城市
      });
      //实例化自动完成
      var ap = new qq.maps.place.Autocomplete(document.getElementById('place'));

      let markerlast = new qq.maps.Marker({
        position: map.center,
        map: map,
      });

      var keyword = '';
      let markers = []; //用户搜索后显示的点的集合

      //调用Poi检索类。用于进行本地检索、周边检索等服务。
      var searchService = new qq.maps.SearchService({
        complete: (results) => {
          if (results.type === 'CITY_LIST') {
            searchService.setLocation(results.detail.cities[0].cityName);
            searchService.search(keyword);
            return;
          }
          console.log(results);
          var pois = results.detail.pois;
          this.setState({
            address: pois[0].address,
            lat: pois[0].latLng.lat,
            lng: pois[0].latLng.lng,
          });

          var latlngBounds = new qq.maps.LatLngBounds();
          for (var i = 0, l = 1; i < l; i++) {
            var poi = pois[i];
            // console.log(poi);
            latlngBounds.extend(poi.latLng);
            var marker = new qq.maps.Marker({
              map: map,
              position: poi.latLng,
              address: poi.latLng.address,
            });

            marker.setTitle(poi.name);
            markers.push(marker);
          }
          map.fitBounds(latlngBounds);
        },
      });

      this.markers = markers;

      //添加监听事件
      console.log(qq.maps.event);
      qq.maps.event.addListener(ap, 'confirm', function (res) {
        keyword = res.value;
        searchService.search(keyword);
      });

      qq.maps.event.addListener(map, 'click', (event) => {
        // 清除初始化位置

        markerlast.position = event.latLng;
        markerlast.setMap(null);
        // 获取经纬度位置

        // 绘制点击的点
        let marker = new qq.maps.Marker({
          position: event.latLng,
          map: map,
        });

        console.log(marker);
        this.setState({
          lat: marker.position.lat,
          lng: marker.position.lng,
        });
        // 添加监听事件   获取鼠标单击事件
        qq.maps.event.addListener(map, 'click', function (event) {
          marker.setMap(null);
        });
        // 清空上一次搜索结果
        Array.from(this.markers).forEach((marker) => {
          marker.setMap(null);
        });
      });
    });
  };

  handleAdress = (v) => {
    console.log(v.target.value);
    this.setState({
      ccc: v.target.value,
    });
  };
  yourCallbackName = (e) => {
    console.log(e);
  };
  render() {
    return (
      <div>
         <Modal title="场所定位" visible onOk={this.handleOk} onCancel={this.handleCancel} width={800}>
          {/* <AutoComplete style={{ width: 200 }} placeholder="input here" options={[]} /> */}
          <div>
            <Input
              autoComplete="off"
              id="asdasda"
              placeholder={'请输入关键词搜索'}
              value={this.state.locationValue}
              onFocus={(e) => {
                this.setState({ asd: true, anchorEl: e.target });
              }}
              // onBlur={handleClose}
              onChange={(e) => {
                this.setState({
                  locationValue: e.target.value,
                });
                fetch(
                  `/ws/place/v1/suggestion?key=7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH&keyword=${e.target.value}`,
                  {
                    method: 'GET',
                    headers: {
                      'Content-Type': 'application/json',
                      'Access-Control-Allow-Methods': 'GET,POST', // 设置允许跨域的域名
                      'Access-Control-Allow-Origin': '*', // 设置允许跨域的域名
                    },
                  },
                )
                  .then((response) => response.json())
                  .then((data) => {
                    this.setState(
                      {
                        searchList: data.data ? data.data : [],
                      },
                      () => {
                        console.log(this.state.searchList);
                      },
                    );
                    // 处理从服务端返回的数据
                  })
                  .catch((error) => {
                    console.error(error);
                  });
              }}
            />
            <Popper
              open={this.state.asd}
              anchorEl={this.state.anchorEl}
              style={{ zIndex: 9999999 }}
            >
              <Paper>
                {this.state.searchList.map((option, index) => (
                  <MenuItem
                    key={index}
                    // selected={option === selected}
                    onClick={() => {
                      option.name = option.title;
                      this.setState(
                        {
                          asd: false,
                          info: option,
                          lat: option.location.lat,
                          lng: option.location.lng,
                          locationValue: option.address + option.title,
                        },
                        () => {
                          this.initMap();
                        },
                      );
                    }}
                  >
                    {option.title}
                    <span style={{ color: '#e0e0e0', fontSize: 15 }}>({option.address})</span>
                  </MenuItem>
                ))}
              </Paper>
            </Popper>
            {/* <Input
              type="text"
              // id="place"
              placeholder="请输入地点名称"
              value={this.state.name}
              onChange={(e) => {
                // $.ajax({
                //   type: 'GET',
                //   url: `https://apis.map.qq.com/ws/place/v1/suggestion?key=7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH&keyword=${e.target.value}`,
                //   dataType: 'jsonp',
                //   jsonp: 'callback',
                //   jsonpCallback: this.yourCallbackName, // 可选，指定回调函数名
                //   success: function (result) {
                //     console.log(result);
                //   },
                //   error: function (xhr, errorType, error) {
                //     console.error(error);
                //   },
                // });
                fetch(
                  `/ws/place/v1/suggestion?key=7EZBZ-DNNWZ-L7HXL-TPRJ5-S5PJF-LBFEH&keyword=${e.target.value}`,
                  {
                    method: 'GET',
                    headers: {
                      'Content-Type': 'application/json',
                      'Access-Control-Allow-Methods': 'GET,POST', // 设置允许跨域的域名
                      'Access-Control-Allow-Origin': '*', // 设置允许跨域的域名
                    },
                  },
                )
                  .then((response) => response.json())
                  .then((data) => {
                    console.log(data);
                    // 处理从服务端返回的数据
                  })
                  .catch((error) => {
                    console.error(error);
                  });
              }}
              suffix={
                <SearchOutlined
                  onClick={() => {
                    this.setState({ lat: 23.021016, lng: 112.751884 }, () => {
                      this.initMap();
                    });
                  }}
                  style={{ color: '#ccc' }}
                />
              }
            /> */}
            <div id="container" style={{ height: 500, marginTop: 15 }} />
          </div>
        </Modal>
      </div>
    );
  }
}

export default connect()(Maps);
