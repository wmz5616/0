import React from 'react';
import { PlusOutlined } from '@ant-design/icons';
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
  Radio,
  Switch,
  TimePicker,
  Alert,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { history, connect, Link } from 'umi';
import ImgCrop from 'antd-img-crop';
import { Map, Marker } from 'react-amap';
import CKEditor from 'react-ckeditor-wrapper';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import moment from 'moment';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
// import { thisExpression } from '@babel/types';
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所基础信息
let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const { isOver, connectDragSource, connectDropTarget, moveRow, ...restProps } = this.props;
    const style = { ...restProps.style, cursor: 'move' };

    let { className } = restProps;
    if (isOver) {
      if (restProps.index > dragingIndex) {
        className += ' drop-over-downward';
      }
      if (restProps.index < dragingIndex) {
        className += ' drop-over-upward';
      }
    }

    return connectDragSource(
      connectDropTarget(<tr {...restProps} className={className} style={style} />),
    );
  }
}

const rowSource = {
  beginDrag(props) {
    dragingIndex = props.index;
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
}))(
  DragSource('row', rowSource, (connect) => ({
    connectDragSource: connect.dragSource(),
  }))(BodyRow),
);

class Hairstylist extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    thumbnailFileList: [],
    carouselFileList: [],
    gymList: [],
    siteList: [],
    isOpen: false,
    locations: {},
    longitude: 113.880469,
    latitude: 22.889404,
    deskGetNumber: true,
    deskDisplay: true,
    serviceLists: [],
    info: {},
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    this.getRecordInfo();
    // const { edit, id } = this.props;
    // this.getData();
    // if (edit == 1) {
    //   this.props.dispatch({
    //     type: 'myModel/getSetData',
    //     payload: {
    //       searchId: id,
    //     },
    //     url: `/ddql/shop/selectById`,
    //     method: 'POST',
    //     myData: (res) => {
    //       if (res && res.code === 10000) {
    //         const values = res.data;
    //         const carouselFileList = [];
    //         const carouselImageUrls = values.carouselImageUrls
    //           ? JSON.parse(values.carouselImageUrls)
    //           : [];
    //         carouselImageUrls.map((ress, index) => {
    //           carouselFileList.push({
    //             uid: String(index + 1),
    //             name: `image${index}.png`,
    //             status: 'done',
    //             url: ress,
    //             response: { data: { url: ress } },
    //           });
    //         });
    //         this.formRef.current.setFieldsValue({
    //           name: values.name,
    //           time: [dayjs(values.startTime, 'HH:mm'), dayjs(values.endTime, 'HH:mm')],
    //           deskGetNumberReason: values.deskGetNumberReason,
    //           name: values.name,
    //           serveTypes: values.serveTypeList
    //             ? values.serveTypeList.map((rsa) => rsa.id)
    //             : undefined,
    //           // thumbnailUrl: this.state.thumbnailFileList[0].response.data.url,
    //           // carouselImageUrls: this.state.carouselFileList.map((res) => res.response.data.url),
    //         });
    //         this.setState({
    //           deskDisplay: values.deskDisplay == 1 ? true : false,
    //           deskGetNumber: values.deskGetNumber == 1 ? true : false,
    //           id: values.id,
    //           thumbnailFileList: [
    //             {
    //               uid: '1',
    //               name: 'image.png',
    //               status: 'done',
    //               url: values.thumbnailUrl,
    //               response: { data: { url: values.thumbnailUrl } },
    //             },
    //           ],
    //           carouselFileList: carouselFileList,
    //           locations: {
    //             name: values.locationName,
    //             location: {
    //               lng: values.location.split(',')[0],
    //               lat: values.location.split(',')[1],
    //             },
    //           },
    //           content: values.description,
    //           locationValue: values.locationName,
    //         });
    //       } else {
    //         message.error(res.message);
    //         // this.setState({ isSelectForm: true });
    //       }
    //     },
    //   });
    //   // this.formRef.current.setFieldsValue({
    //   //   name: info.name, //名称
    //   //   address: info.address, //地址
    //   //   open_time: info.open_time, //开放时间
    //   //   contact: info.contact, //联系电话
    //   //   sport_type: info.sport_types.map((res) => res.sport_type.id), //运动类型
    //   //   gym_service: info.gym_services.map((res) => res.gym_service.id), //场所服务
    //   //   type: info.type, //场所类型
    //   //   plague_pvt: x1,
    //   //   community: info.community.toString(), //社区类型
    //   //   leave_check: info.leave_check,
    //   //   is_quick: info.is_quick ? info.is_quick : 0,
    //   //   quick_gym_id: info.quick_gym_id == 0 ? undefined : info.quick_gym_id,
    //   //   quick_site_id: info.quick_site_id == 0 ? undefined : info.quick_site_id,
    //   // });
    // }
  }

  getRecordInfo = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.props.id,
      },
      url: `/ddql/team/selectVerificationRecord`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          if (res.data) {
            const values = res.data;
            const thumbnailFileList = [];
            const carouselImageUrls = values.licenseImage ? values.licenseImage.split(';') : [];
            carouselImageUrls.map((ress, index) => {
              thumbnailFileList.push({
                uid: String(index + 1),
                name: `image${index}.png`,
                status: 'done',
                url: ress,
                response: { data: { url: ress } },
              });
            });
            const carouselFileList = [];
            const carouselImgURLs = values.additionPicture ? values.additionPicture.split(';') : [];
            carouselImgURLs.map((ress, index) => {
              carouselFileList.push({
                uid: String(index + 1),
                name: `image${index}.png`,
                status: 'done',
                url: ress,
                response: { data: { url: ress } },
              });
            });
            this.formRef.current.setFieldsValue({
              licenseType: values.licenseType,
              deskGetNumberReason: values.deskGetNumberReason,
              verificationType: 0,
              type: values.type,
              contactPhone: values.contactPhone,
              contactEmail: values.contactEmail,
              licenseImageList: values.licenseImageList,
              additionPictureList: values.additionPictureList,
            });
            this.setState({
              carouselFileList,
              thumbnailFileList,
              info: res.data,
            });
          }
        } else {
          message.error(res.message);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  updateContent = (value, index) => {
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
  handleCancelz = () => {
    this.setState({
      map: false,
    });
  };
  handleOkz = () => {
    this.setState(
      {
        map: false,
      },
      () => {
        this.setState({ locationValue: this.state.locations.name });
      },
    );
  };
  handleCancel = () => {
    this.setState({
      addGroupModalVisible: false,
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
          if (result.poiList.pois.length == 0) {
            message.info('未搜索到相关地址，请重新输入');
            return;
          }
          this.setState(
            {
              AddressDetails:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location,
              latitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lat,
              longitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lng,
              locations: result.poiList && result.poiList.pois && result.poiList.pois[0],
            },

            () => {},
          );
        });
      });
    }, 150);
  };
  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        const { dispatch } = this.props;
        // 场所服务列表
        dispatch({
          type: 'myModel/getSetData',
          payload: {},
          url: `/ddql/shop/type/lists`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              this.setState({
                serviceLists: res.data,
              });
            } else {
              message.error(res.message);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

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

  getQrcode = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: this.props.info.id,
      },
      url: `/api/admin/stadium/quick/code`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          window.open(
            window.location.hostname == 'admin.sshtyt.cn'
              ? 'https://api.sshtyt.cn' + res.data
              : 'http://39.108.167.61:8006' + res.data,
            '_blank',
          );
          // let xhr = new XMLHttpRequest()
          // let fileName = `` // 文件名称
          // xhr.open('GET',res.data, true)
          // xhr.responseType = 'blob'
          // xhr.onload = function () {
          //     if (this.status === 200) {
          //         let type = xhr.getResponseHeader('Content-Type')

          //         let blob = new Blob([this.response], { type: type })
          //         if (typeof window.navigator.msSaveBlob !== 'undefined') {
          //             window.navigator.msSaveBlob(blob, fileName)
          //         } else {
          //             let URL = window.URL || window.webkitURL
          //             let objectUrl = URL.createObjectURL(blob)
          //             if (fileName) {
          //                 var a = document.createElement('a')
          //                 // safari doesn't support this yet
          //                 if (typeof a.download === 'undefined') {
          //                     window.location = objectUrl
          //                 } else {
          //                     a.href = objectUrl
          //                     a.download = fileName
          //                     document.body.appendChild(a)
          //                     a.click()
          //                     a.remove()
          //                 }
          //             } else {
          //                 window.location = objectUrl
          //             }
          //         }
          //     }
          // }
          // xhr.send()
        } else {
          message.error(res.message);
        }
      },
    });
  };

  handleOk = () => {
    this.formRefs.current.validateFields().then((values) => {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          status: values.status,
          id: this.state.info.id,
        },
        url: '/ddql/team/auditVerificationRecord',
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.getRecordInfo();
            this.setState({
              auditModal: false,
            });
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  onFinish = (values) => {
    const { info, edit, xxxx } = this.props;
    const params = {
      teamId: this.props.id,
      licenseType: 0,
      deskGetNumberReason: values.deskGetNumberReason,
      verificationType: 0,
      type: 1,
      contactPhone: values.contactPhone ? values.contactPhone : '',
      contactEmail: values.contactEmail ? values.contactEmail : '',
      licenseImageList: this.state.thumbnailFileList.map((res) => res.response.data.url),
      additionPictureList: this.state.carouselFileList.map((res) => res.response.data.url),
    };
    if (this.state.info.id) {
      params.id = this.state.info.id;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: this.state.info.id
        ? '/ddql/team/updateVerificationRecord'
        : '/ddql/team/addVerificationRecord',
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getRecordInfo();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
    ({ file, fileList }) => {
      this.setState({ [type]: fileList }, () => {
        const { response = {} } = file;
        if (response.code == 10000) {
          const data = this.state[type];
          if (data[data.length - 1]) {
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
            });
          }
        }
      });
    };

  // 通用的预览处理
  handlePreview = async (file) => {
    let src = file.url;
    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);
        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewImage: src,
      previewOpen: true,
    });
  };

  // 关闭预览
  handleCancelPreview = () => {
    this.setState({ previewOpen: false });
  };

  addGroup = () => {
    const data = JSON.parse(JSON.stringify(this.state.serviceLists));
    data.push({ ddql: 0, perm: 0, sort: data.length + 1 });
    this.setState({ serviceLists: data });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { serviceLists } = this.state;
    const dragRow = serviceLists[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        serviceLists: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
    );
  };

  submitService = () => {
    const params = {
      serveTypeList: this.state.serviceLists,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/shop/type/add`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const { serviceLists = [], motionTypelist = [] } = this.state;
    const { thumbnailFileList, carouselFileList, previewImage, previewOpen } = this.state;
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '类型名称',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data[index].name = e.target.value;
                this.setState({
                  serviceLists: data,
                });
              }}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '去美发',
        dataIndex: 'ddql',
        render: (status, record, index) => {
          return (
            <Switch
              unCheckedChildren="关"
              checkedChildren="开"
              checked={status}
              onChange={(value) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data.map((resd, indexs) => {
                  if (index == indexs) {
                    resd.ddql = value ? 1 : 0;
                    if (value) {
                      resd.perm = 0;
                    }
                  } else if (value) {
                    resd.ddql = 0;
                  }
                });
                this.setState({
                  serviceLists: data,
                });
              }}
            />
          );
        },
      },
      {
        title: '去染发',
        dataIndex: 'perm',
        render: (status, record, index) => {
          return (
            <Switch
              unCheckedChildren="关"
              checkedChildren="开"
              checked={status}
              onChange={(value) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data.map((resd, indexs) => {
                  if (index == indexs) {
                    resd.perm = value ? 1 : 0;
                    if (value) {
                      resd.ddql = 0;
                    }
                  } else if (value) {
                    resd.perm = 0;
                  }
                });
                this.setState({
                  serviceLists: data,
                });
              }}
            />
          );
        },
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <div>
            <span
              className="mL15 red"
              onClick={() => {
                if (record.id) {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteId: record.id,
                    },
                    url: `/ddql/shop/type/delete`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.getData();
                      } else {
                        message.error(res.msg);
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
                } else {
                  const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                  data.splice(index, 1);
                  data.map((resd, index) => {
                    resd.sort = index + 1;
                  });
                  this.setState({
                    serviceLists: data,
                  });
                }
              }}
            >
              删除
            </span>
          </div>
        ),
      },
    ];
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

    const props = {
      grid: false,
      width: 316,
      height: 246,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
    };

    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };

    return (
      <div style={{ paddingTop: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <h1 style={{ fontWeight: '600', fontSize: '18px' }}>资质认证</h1>
            <div
              style={{
                marginLeft: 6,
                fontSize: 18,
                color:
                  this.state.info.status == undefined || this.state.info.status == 0
                    ? 'rgba(245, 154, 35, 0.84)'
                    : this.state.info.status == 1
                    ? 'rgba(17, 175, 33, 0.847)'
                    : 'rgba(217, 0, 27, 0.84)',
              }}
            >
              {this.state.info.status == undefined
                ? '未认证'
                : this.state.info.status == 0
                ? '审核中'
                : this.state.info.status == 1
                ? '已通过'
                : '已驳回'}
            </div>
          </div>
          <Button style={{ marginRight: 30 }} onClick={() => history.goBack()}>
            返回
          </Button>
        </div>
        <Form
          ref={this.formRef}
          layout="vertical"
          wrapperCol={{
            span: 14,
          }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
        >
          {/* <Form.Item label="证件类型" name="licenseType" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value={0}>营业执照</Radio>
              <Radio value={1}>法人证书</Radio>
            </Radio.Group>
          </Form.Item> */}
          {/* 缩略图上传 */}
          <div style={{ color: 'rgba(0, 0, 0, 0.427450980)' }}>
            要根据团体类型输入资质认证所需资料
          </div>
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>营业执照或法人证书
              </span>
            }
          >
            <div style={{ color: 'rgba(0, 0, 0, 0.427450980)' }}>
              图片要求：大小不超过2M,分辨率不低于720*1280，必须为最新的纸质证件原件拍照或彩色扫描件，
              <span style={{ color: 'rgba(0, 0, 0, 0.427450980)', fontWeight: 'bold' }}>
                若未使用最新证件照，将无法通过备案审核；
              </span>
              须证件四周圆角及卡正边缘清晰：若添加水印，须添加在证件空白位置不遮挡文字、图像信息，水印内容符合资质认证且不添加有效期。
            </div>
            <Form.Item noStyle>
              <Upload
                action="/ddql/file/upload"
                listType="picture-card"
                fileList={thumbnailFileList}
                onChange={this.handleUploadChange('thumbnailFileList')}
                onPreview={this.handlePreview}
                beforeUpload={beforeUpload}
                accept="image/jpeg,image/png"
                headers={{ token: localStorage.getItem('token') }}
              >
                {thumbnailFileList.length < 10 && uploadButton}
              </Upload>
            </Form.Item>
            {/* <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span> */}
          </Form.Item>

          {/* 轮播图上传 */}
          <Form.Item label={<span>其他附件</span>}>
            <Form.Item noStyle>
              <Upload
                action="/ddql/file/upload"
                listType="picture-card"
                fileList={carouselFileList}
                onChange={this.handleUploadChange('carouselFileList')}
                onPreview={this.handlePreview}
                beforeUpload={beforeUpload}
                accept="image/jpeg,image/png"
                headers={{ token: localStorage.getItem('token') }}
              >
                {carouselFileList.length < 10 && uploadButton}
              </Upload>
            </Form.Item>
          </Form.Item>

          {/* 预览模态框 */}
          <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          {/* <Form.Item label="审核方式" name="type" rules={[{ required: true }]}>
            <Radio.Group>
              <Radio value={0}>正常审核</Radio>
              <Radio value={1}>人工审核</Radio>
            </Radio.Group>
          </Form.Item> */}
          {/* <Form.Item
            label="资质类型"
            name="verificationType"
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Select className="norBorder" placeholder="请选择">
              <Option value={0}>企事单位</Option>
              <Option value={1}>政府部门</Option>
              <Option value={2}>家庭</Option>
              <Option value={3}>朋友</Option>
            </Select>
          </Form.Item> */}
          <Form.Item
            label="联系电话"
            name="contactPhone"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="邮箱"
            name="contactEmail"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item>
            <Button style={{ marginRight: 20 }} onClick={() => history.goBack()}>
              取消
            </Button>
            <Button style={{ marginRight: 20 }} type="primary" htmlType="submit">
              提交
            </Button>
            {(this.state.info.status == 0 || this.state.info.status == 2) && (
              <Button type="primary" onClick={() => this.setState({ auditModal: true })}>
                审核
              </Button>
            )}
          </Form.Item>
        </Form>

        <Modal
          title="审核"
          visible={this.state.auditModal}
          onOk={this.handleOk}
          onCancel={() => this.setState({ auditModal: false })}
        >
          <Form ref={this.formRefs}>
            <Form.Item
              label="审核结果"
              name="status"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <Radio.Group>
                <Radio value={1}>通过</Radio>
                <Radio value={2}>驳回</Radio>
              </Radio.Group>
            </Form.Item>
          </Form>
        </Modal>
      </div>
    );
  }
}
export default connect()(Hairstylist);
