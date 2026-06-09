import Map from '@/components/Map';
import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { PlusOutlined } from '@ant-design/icons';
import {
  Button,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Switch,
  Upload,
} from 'antd';
import ImgCrop from 'antd-img-crop';
import update from 'immutability-helper';
import React from 'react';
import CKEditor from 'react-ckeditor-wrapper';
const { TextArea } = Input;
// 场所基础信息

class AddShangquanModal extends React.Component {
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
    locationInfo: {},
    locationStr: '',
  };

  componentDidMount() {
    this.getData();
  }

  getData = async () => {
    const { edit, id } = this.props;
    if (!this.props.isAdd) {
      const res = await post(`/guzhe/supermarket/select`, {
        searchId: id,
      });
      if (res && res.code == 10000) {
        console.log(res.data.list[0]);
        const values = res.data?.list[0] || {};
        const carouselFileList = [];
        const carouselImageUrls = values.logoImage;
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
          coverImage: this.state.thumbnailFileList.map(
            (cz) => cz.response.data.url,
          )[0],
          logoImage: this.state.carouselFileList.map(
            (cz) => cz.response.data.url,
          ),
          status: values.status == 1 ? true : false,
          name: values.name,
          phone: values.phone,
          businessHours: values.businessHours,
          locationName: values.address,
        });
        this.setState({
          content: values.description,
          id: values.id,
          locationInfo: { address: values.address },
          locationStr: values.latitude ? `${values.longitude},${values.latitude}` : '',
          thumbnailFileList: [
            {
              uid: '1',
              name: 'image.png',
              status: 'done',
              url: values.coverImage,
              response: { data: { url: values.coverImage } },
            },
          ],
          carouselFileList: carouselFileList,
          locationValue: values.address,
          content: values.description,
        });
      } else {
        message.error(res?.msg);
      }
    }
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
          locationValue:
            this.state.locationInfo.address + this.state.locationInfo.name,
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
        let placeSearch = new window.AMap.PlaceSearch({
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
        placeSearch.search(
          document.getElementById('tipinputs').value,
          (res, result) => {
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
                locations:
                  result.poiList &&
                  result.poiList.pois &&
                  result.poiList.pois[0],
                location: `${result.poiList.pois[0].location.lng},${result.poiList.pois[0].location.lat}`,
              },

              () => { },
            );
          },
        );
      });
    }, 150);
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
      ({ file, fileList }) => {
        const list = fileList.filter(i => i.status == 'done' || i.status == 'uploading');
        this.setState({ [type]: list }, () => {
          const { response = {} } = file;
          if (response.code == 10000 && file.status != 'removed') {
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
    this.formRef.current.validateFields().then(async (values) => {
      const params = {
        coverImage: this.state.thumbnailFileList.map(
          (cz) => cz.response.data.url,
        )[0],
        logoImage: this.state.carouselFileList.map(
          (cz) => cz.response.data.url,
        ),
        status: values.status ? 1 : 0,
        phone: values.phone,
        businessHours: values.businessHours,
        name: values.name,
        longitude: this.state.locationStr?.split(',')[0] || undefined,
        latitude: this.state.locationStr?.split(',')[1] || undefined,
        address: this.state.locationValue,
        description: this.state.content,
      };
      if (!this.props.isAdd) {
        params.id = this.state.id;
      }
      const res = await post(
        this.props.isAdd
          ? `/guzhe/supermarket/add`
          : `/guzhe/supermarket/update`,
        params,
      );
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.props.getData();
        this.props.handleCancel();
      } else {
        message.error(res?.msg);
      }
    });
  };
  // 裁剪前获取图片尺寸并计算宽高比
  handleBeforeCrop = (file) => {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => {
        const aspect = img.width / img.height;
        this.setState({ cropAspect: aspect });
        URL.revokeObjectURL(img.src);
        resolve(true);
      };
      img.onerror = () => {
        resolve(true);
      };
      img.src = URL.createObjectURL(file);
    });
  };

  render() {
    const { thumbnailFileList, carouselFileList, previewImage, previewOpen } =
      this.state;

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/guzhe/file/upload',

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
          if (info.file.status == 'done') {
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
      id: false,
      aspect: this.state.cropAspect,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
      beforeCrop: this.handleBeforeCrop,
    };

    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng =
        file.type == 'image/jpeg' || file.type == 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        message.error('仅支持上传小于2MB的图片！');
      }
      return isJpgOrPng && isLt2M;
    };

    return (
      <Modal
        zIndex={101}
        style={{ minWidth: '50%' }}
        open
        onOk={this.submitService}
        onCancel={() => this.props.handleCancel()}
        title={this.props.isAdd ? '新增商超' : '编辑商超'}
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
          autoComplete="off"
        >
          {/* 缩略图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>商超封面图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  action="/guzhe/file/upload"
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
            <span style={{ color: '#ccc' }}>
              支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>

          {/* 轮播图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>商超轮播图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  action="/guzhe/file/upload"
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
          <Modal
            open={previewOpen}
            footer={null}
            onCancel={this.handleCancelPreview}
          >
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          <Form.Item
            label="商超名称"
            name="name"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>商超地址
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
              <Button
                type="default"
                className="darkBlue-btn"
                onClick={this.showModal}
              >
                选择位置
              </Button>
            </div>
          </Form.Item>
          <Form.Item
            label="联系电话"
            name="phone"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="营业时间"
            name="businessHours"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="启用状态"
            name="status"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={true}
          >
            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
          </Form.Item>
          <Form.Item
            label='商超介绍'
            rules={[{ required: false, message: '请输入!' }]}
          >
            <div
              style={{ position: 'relative', marginTop: '-15px', width: 600 }}
            >
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
                      items: [
                        'Cut',
                        'Copy',
                        'Paste',
                        'PasteText',
                        'PasteFromWord',
                        '-',
                      ],
                    },
                    {
                      name: 'basicstyles',
                      items: [
                        'Bold',
                        'Italic',
                        'Underline',
                        '-',
                        'CopyFormatting',
                      ],
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
                    locationInfo &&
                      locationInfo.location.lat &&
                      locationInfo.location.lng
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
export default AddShangquanModal;
