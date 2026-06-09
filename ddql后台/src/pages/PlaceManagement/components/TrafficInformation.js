import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
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
import { PageContainer } from '@ant-design/pro-layout';
import CKEditor from 'react-ckeditor-wrapper';
import { history, connect, Link } from 'umi';
import { Map, Marker } from 'react-amap';
import ImgCrop from 'antd-img-crop';

import Maps from './Maps'; //场馆配置

// import { thisExpression } from '@babel/types';
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 交通信息

class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    fileList: [],
  };

  componentDidMount() {
    const { info } = this.props;
    // this.getData();
    console.log(this.props);
    if (info) {
      this.setState({
        content: info.traffic_guide,
        lngs: info.lat,
        lats: info.lng,
        locations: info.location,
        address:info.location,
      });
      this.formRef.current.setFieldsValue({
        locationx:  info.lat? `${info.lat},${info.lng}`:undefined,
        location: info.location,
      });
    }
  }

  selectAddress = {
    // created必须要拥有,用来初始化创建相应对象
    created: () => {
      let auto;
      let placeSearch;
      window.AMap.plugin('AMap.Autocomplete', () => {
        auto = new window.AMap.Autocomplete({
          input: 'tipinput',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
          // city: '昆明', // 限制为只能搜索当前地区的位置
          outPutDirAuto: true,
          // type: '汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施'
        });
      });
      // 创建搜索实例
      window.AMap.plugin('AMap.PlaceSearch', () => {
        placeSearch = new window.AMap.PlaceSearch({
          input: 'tipinput',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
      });

      window.AMap.event.addListener(auto, 'select', (e) => {
        placeSearch.search(e.poi.name);
      });
    },
  };

  onFinish = (values) => {
    const { info,getData } = this.props;

    // console.log(values.locationx.split(',')[0],values.locationx.split(',')[1])
    // values.locationx
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: info.id,
        lat: values.locationx.split(',')[0],
        lng: values.locationx.split(',')[1],
        traffic_guide: this.state.content,
        // location: this.state.locations,
        location: this.state.address,
        item: 'locate',
      },
      url: `/api/admin/stadium/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 200) {
          message.success(res.message);
          getData()
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  showModal = () => {
    this.setState(
      {
        map: true,
      },
      () => {
        this.toolEvents = {
          created: (tool) => {
            this.tool = tool;
          },
        };
        // this.mapPlugins = ['ToolBar'];
        this.mapCenter = { longitude: 120, latitude: 35 };
        this.markerPosition = { longitude: 121, latitude: 36 };
      },
    );
  };

  showModalss = () => {
    this.setState({
      maps: true,
    });
  };

  handleCancel = () => {
    this.setState({
      maps: false,
    });
  };

  handleOkz = () => {
    this.setState(
      {
        map: false,
      },
      () => {
        this.formRef.current.setFieldsValue({
          locationx: `${this.state.lats},${this.state.lngs}`,
        });
      },
    );
  };

  handleCancelz = () => {
    this.setState({
      map: false,
    });
  };
  getAddress = () => {
    setTimeout((_) => {
      window.AMap.plugin('AMap.PlaceSearch', () => {
        var placeSearch = new window.AMap.PlaceSearch({
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
        placeSearch.search(document.getElementById('tipinput').value, (res, result) => {
          console.log(result);
          this.setState(
            {
              AddressDetails:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location,
              lats:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lat,
              lngs:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lng,
              locations:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].name,
            },
            () => {
              // this.toolEvents = {
              //   created: (tool) => {
              //     this.tool = tool;
              //   },
              // };
              // this.mapCenter = {
              //   longitude: this.state.lngs,
              //   latitude: this.state.lats,
              // };
              // this.markerPosition = {
              //   longitude: this.state.lngs,
              //   latitude: this.state.lats,
              // };
              // this.setState({
              //   xxxx:false
              // },()=>{
              //  this.setState({
              //   xxxx:true
              //  })
              // })
            },
          );
        });
      });
    }, 150);
  };

  addressDetails = (address, lat, lng) => {
    console.log(address, lat, lng);
    this.setState({
      address: address,
      lat: String(lat).substr(0,9),
      lng:String(lng).substr(0,10),
    });
    this.formRef.current.setFieldsValue({
      locationx: `${String(lat).substr(0,9)},${String(lng).substr(0,10)}`,
      location: address,
    });
  };

  render() {
    const {
      serviceLists = [],
      motionTypelist = [],
      serviceTypeLists = [],
      fileList = [],
    } = this.state;

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    return (
      <div style={{ paddingTop: 24 }}>
        <h1 style={{ fontWeight: '600', fontSize: '18px' }} >
          交通信息
        </h1>

        <Form
          ref={this.formRef}
          layout="vertical"
          style={{ width: 800 }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
        >
          <Form.Item
            label="地理位置"
            name="locationx"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Input placeholder="请输入经纬度或点击进行地图定位选择" style={{ width: 670 }} />
            {/* <Input placeholder="请选择地理位置" /> */}
          </Form.Item>
     
          <Button
            type="primary"
            onClick={this.showModalss}
            style={{ position: 'absolute', marginTop: '-55px', marginLeft: 680, width: 120 }}
          >
            地图定位
          </Button> 

          <div style={{ color: '#ccc', marginTop: '-20px', marginBottom: 15 }}>
            经纬度格式参考：23.021016,113.751884
          </div>

          {/* <Form.Item
            label="场所地址"
            name="location"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item> */}

          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>交通指引
              </span>
            }
          >
            <div style={{ position: 'relative', marginTop: '-15px' }}>
              <Upload
                showUploadList={false}
                accept={'image/*'}
                // headers={{
                //   Authorization: getToken()
                //
                {...uploadProps(1)}
              >
                <div className="zxc" style={{ left: 10 }} />
              </Upload>
              <CKEditor
                ref={(ckeditor) => {
                  this.ckeditor = ckeditor;
                }}
                value={this.state.content}
                config={{
                  toolbar: [
                    {
                      name: 'clipboard',
                      items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                    },
                    {
                      name: 'basicstyles',
                      items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
                    },
                    {
                      name: 'paragraph',
                      items: [
                        'NumberedList',
                        'BulletedList',
                        '-',
                        'Outdent',
                        'Indent',
                        '-',
                        'JustifyLeft',
                        'JustifyCenter',
                        'JustifyRight',
                        'JustifyBlock',
                        '-',
                      ],
                    },
                    { name: 'links', items: ['Link', 'Unlink'] },
                    { name: 'insert', items: ['Image', 'Table'] },
                    { name: 'styles', items: ['Font', 'FontSize'] },
                    { name: 'colors', items: ['TextColor', 'BGColor'] },
                    { name: 'tools', items: ['Maximize'] },
                  ],
                  extraPlugins: 'placeholder',
                  height: 250,
                  uploadUrl: '/home/media/upload',
                  removeDialogTabs: 'image:advanced;link:advanced',
                }}
                onChange={this.updateContent}
              />
            </div>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit">
              提交
            </Button>
          </Form.Item>
        </Form>

        <Modal
          title="选择位置"
          visible={this.state.map}
          onOk={this.handleOkz}
          onCancel={this.handleCancelz}
          width={800}
        >
          <div style={{ width: '100%', height: '400px' }}>
            <input
              onBlur={this.getAddress}
              onChange={this.input}
              id="tipinput"
              style={{ marginBottom: 20, width: '100%' }}
            />
            {/* <Map
                events={this.selectAddress}
                plugins={['Autocomplete','ToolBar']}
                center={this.mapCenter}
                amapkey={'788e08def03f95c670944fe2c78fa76f'}
                zoom={15}
              >
                 {this.state.xxxx && <Marker position={this.markerPosition} />}
              
               </Map> */}

            <Map
              events={this.selectAddress}
              plugins={['Autocomplete', 'ToolBar', 'Scale']}
              // plugins={['ToolBar', 'Scale']}
              amapkey={'788e08def03f95c670944fe2c78fa76f'}
              zoom={15}
            >
              <Marker position={['lng', 'lat']} />
            </Map>
          </div>
        </Modal>

        {this.state.maps && (
          <Maps handleCancel={this.handleCancel} addressDetails={this.addressDetails} />
        )}
      </div>
    );
  }
}

export default connect()(Login);
