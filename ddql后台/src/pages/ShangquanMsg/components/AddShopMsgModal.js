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
  InputNumber,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { history, connect, Link } from 'umi';
import ImgCrop from 'antd-img-crop';
import Map from '@/components/Map';
import CKEditor from 'react-ckeditor-wrapper';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import moment from 'moment';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所基础信息

class AddShopMsgModal extends React.Component {
  formRef = React.createRef();
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
  };

  componentDidMount() {
    const { edit, id } = this.props;
    if (!this.props.isAdd) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: id,
        },
        url: `/ddql/business/shop/selectById`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            const values = res.data.shop;
            const carouselFileList = [];
            const carouselImageUrls = values.galleryImages;
            carouselImageUrls.map((ress, index) => {
              carouselFileList.push({
                uid: String(index + 1),
                name: `image${index}.png`,
                status: 'done',
                url: ress,
                response: { data: { url: ress } },
              });
            });
            this.formRef.current.setFieldsValue({
              status: values.status == 1 ? true : false,
              remark: values.remark,
              name: values.name,
              userName: values.userName ? values.userName : '',
              topRecommend: values.topRecommend,
              recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
              phone: values.phone ? values.phone : '',
              sortOrder: values.sortOrder,
              time: values.startTime
                ? [dayjs(values.startTime, 'HH:mm'), dayjs(values.endTime, 'HH:mm')]
                : undefined,
              circleIds: res.data.circleList.map((x) => x.circleId),
              topStartTime: values.topStartTime ? dayjs(values.topStartTime) : undefined,
              topEndTime: values.topEndTime ? dayjs(values.topEndTime) : undefined,
            });
            this.setState({
              topRecommend: values.topRecommend,
              location: values.location,
              deskDisplay: values.deskDisplay == 1 ? true : false,
              deskGetNumber: values.deskGetNumber == 1 ? true : false,
              id: values.id,
              thumbnailFileList: [
                {
                  uid: '1',
                  name: 'image.png',
                  status: 'done',
                  url: values.coverImageUrl,
                  response: { data: { url: values.coverImageUrl } },
                },
              ],
              carouselFileList: carouselFileList,
              content: values.description,
              locationInfo: { address: values.address },
              locationStr: values.location,
              locationValue: values.address,
            });
          } else {
            message.error(res.message);
            // this.setState({ isSelectForm: true });
          }
        },
      });
      // this.formRef.current.setFieldsValue({
      //   name: info.name, //名称
      //   address: info.address, //地址
      //   open_time: info.open_time, //开放时间
      //   contact: info.contact, //联系电话
      //   sport_type: info.sport_types.map((res) => res.sport_type.id), //运动类型
      //   gym_service: info.gym_services.map((res) => res.gym_service.id), //场所服务
      //   type: info.type, //场所类型
      //   plague_pvt: x1,
      //   community: info.community.toString(), //社区类型
      //   leave_check: info.leave_check,
      //   is_quick: info.is_quick ? info.is_quick : 0,
      //   quick_gym_id: info.quick_gym_id == 0 ? undefined : info.quick_gym_id,
      //   quick_site_id: info.quick_site_id == 0 ? undefined : info.quick_site_id,
      // });
    }
  }

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
    if (!this.state.locationInfo) {
      message.info('请选择所在位置');
      return;
    }
    this.setState(
      {
        map: false,
      },
      () => {
        this.setState({
          locationValue: this.state.locationInfo.address + this.state.locationInfo.name,
        });
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
        placeSearch.search(document.getElementById('tipinputs').value, (res, result) => {
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
              location: `${result.poiList.pois[0].location.lng},${result.poiList.pois[0].location.lat}`,
            },

            () => {},
          );
        });
      });
    }, 150);
  };

  selectAddress = {
    // created必须要拥有,用来初始化创建相应对象
    created: () => {
      let auto;
      let placeSearch;
      window.AMap.plugin('AMap.Autocomplete', () => {
        auto = new window.AMap.Autocomplete({
          input: 'tipinputs',
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
          input: 'tipinputs',
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
    this.formRef.current.validateFields().then((values) => {
      const params = {
        coverImageUrl: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
        galleryImages: this.state.carouselFileList.map((cz) => cz.response.data.url),
        status: values.status ? 1 : 0,
        remark: values.remark,
        name: values.name,
        location: this.state.locationStr,
        userName: values.userName ? values.userName : '',
        topRecommend: values.topRecommend ? 1 : 0,
        recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
        phone: values.phone ? values.phone : '',
        address: this.state.locationValue,
        description: this.state.content,
        sortOrder: values.sortOrder,
        startTime: values.time ? values.time[0].format('HH:mm:00') : undefined,
        endTime: values.time ? values.time[1].format('HH:mm:00') : undefined,
        circleIds: values.circleIds,
        topStartTime: values.topStartTime
          ? values.topStartTime.format('YYYY-MM-DD 00:00:00')
          : undefined,
        topEndTime: values.topEndTime ? values.topEndTime.format('YYYY-MM-DD 23:59:59') : undefined,
      };
      if (!this.props.isAdd) {
        params.id = this.state.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: `/ddql/business/shop/save`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.props.getData();
            this.props.handleCancel();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
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
      width: 690,
      height: 312,
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
      <Modal
        zIndex={101}
        style={{ minWidth: '50%' }}
        open
        onOk={this.submitService}
        onCancel={() => this.props.handleCancel()}
        title={this.props.isAdd ? '新增店铺' : '编辑店铺'}
        // footer={[]}
      >
        <Form
          ref={this.formRef}
          labelCol={{
            span: 4,
          }}
          wrapperCol={{
            span: 16,
          }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
        >
          {/* 缩略图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>店铺封面图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
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
                  {thumbnailFileList.length < 1 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
          </Form.Item>

          {/* 轮播图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>店铺轮播图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
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
                  {carouselFileList.length < 5 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              支持上传最多五张图，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>

          {/* 预览模态框 */}
          <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          <Form.Item label="店铺名称" name="name" rules={[{ required: true, message: '请输入!' }]}>
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>店铺地址
              </div>
            }
            name="location"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Input
                disabled
                value={this.state.locationValue}
                placeholder="请选择位置"
                style={{ marginRight: 8 }}
              ></Input>
              <Button type="default" className="darkBlue-btn" onClick={this.showModal}>
                选择位置
              </Button>
            </div>
          </Form.Item>
          <Form.Item
            label="联系人"
            name="userName"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="联系电话"
            name="phone"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item label="营业时间" name="time" rules={[{ required: false, message: '请选择!' }]}>
            <TimePicker.RangePicker format="HH:mm" />
          </Form.Item>
          <Form.Item
            label="所属商圈"
            name="circleIds"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Select mode="multiple" placeholder="请选择" showSearch optionFilterProp="children">
              {this.props.circleList.map((sxsa) => (
                <Option value={sxsa.id}>{sxsa.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="置顶推荐"
            name="topRecommend"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Radio.Group
              onChange={(e) => {
                this.setState({ topRecommend: e.target.value });
              }}
            >
              <Radio value={1}>是</Radio>
              <Radio value={0}>否</Radio>
            </Radio.Group>
          </Form.Item>
          {this.state.topRecommend == 1 && (
            <Form.Item
              label="置顶开始日期"
              name="topStartTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker />
            </Form.Item>
          )}
          {this.state.topRecommend == 1 && (
            <Form.Item
              label="置顶结束日期"
              name="topEndTime"
              rules={[{ required: false, message: '请选择!' }]}
            >
              <DatePicker />
            </Form.Item>
          )}
          {this.state.topRecommend == 1 && (
            <Form.Item
              label="推荐顺序"
              name="recommendOrder"
              rules={[{ required: true, message: '请输入' }]}
            >
              <InputNumber placeholder="请输入" />
            </Form.Item>
          )}
          <div style={{ color: '#ccc', paddingLeft: '17%', marginBottom: 10, marginTop: -15 }}>
            数值越大，推荐顺序越前
          </div>
          <Form.Item
            label="启用状态"
            name="status"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={true}
          >
            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
          </Form.Item>
          <Form.Item label={<span>店铺介绍</span>} rules={[{ required: true, message: '请输入!' }]}>
            <div style={{ position: 'relative', marginTop: '-15px', width: 600 }}>
              <Upload
                showUploadList={false}
                accept={'image/*'}
                // headers={{
                //   Authorization: getToken()
                //
                {...uploadProps(1)}
              >
                <div className="zxc" />
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
          <Form.Item label="备注" name="remark">
            <TextArea rows={4} placeholder="请输入" />
          </Form.Item>
        </Form>
        {this.state.map && (
          <Modal
            title="选择位置"
            visible
            onOk={this.handleOkz}
            zIndex={102}
            onCancel={this.handleCancelz}
            style={{ paddingBottom: 50, minWidth: 800 }}
          >
            <div style={{ width: '100%', marginBottom: 50 }}>
              <Map
                locationName={this.state.locationStr}
                locationInfo={this.state.locationInfo}
                isAdd={this.props.isAdd}
                onLocationChange={(locationInfo) => {
                  console.log('收到地图数据:', locationInfo);
                  const locationStr =
                    locationInfo && locationInfo.location.lat && locationInfo.location.lng
                      ? `${locationInfo.location.lng},${locationInfo.location.lat}`
                      : '';
                  console.log('收到地图数据:', locationStr);
                  this.setState({
                    locationInfo, // 存储地图数据
                    locationStr,
                  });
                }}
              />
            </div>
          </Modal>
        )}
      </Modal>
    );
  }
}
export default connect()(AddShopMsgModal);
