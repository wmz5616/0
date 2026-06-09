import React from 'react';
import { PlusOutlined, CheckCircleTwoTone } from '@ant-design/icons';
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
  Upload,
  Modal,
  Radio,
  Switch,
  TimePicker,
  Alert,
  InputNumber,
  Table,
} from 'antd';
import { history } from 'umi';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
import ImgCrop from 'antd-img-crop';
import { post } from '@/utils/request';
const { Option } = Select;

class contractPhotos extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    isShouKuan: false,
    thumbnailFileList: [],
  };

  componentDidMount() {
    const { type, id, disabled = false, isContract } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
        isContract,
      },
      () => {
        type != 'add' && this.getData();
      },
    );
  }

  onFinish = async () => {
    // 判断是否填写了图片
    if (!this.state.thumbnailFileList.length) {
      message.info('请上传图片！');
      return;
    }

    const urlList = this.state.thumbnailFileList.map((item) => item.response.data.url);
    let params = {
      shopId: this.state.id,
      urlList,
    }

    if (this.state.isContract) {
      params = {
        searchId: this.state.id,
        searchStrField1: urlList.join(','),
      }
    }

    const res = await post(this.state.isContract ? `/guzhe/shop/contract` : '/guzhe/shop/poster/save', params);
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  getData = async () => {
    this.setState({
      spinning: true,
    });
    const res = await post(this.state.isContract ? `/guzhe/shop/selectById` : '/guzhe/shop/poster/lists', {
      searchId: this.state.id,
    })
    this.setState({
      spinning: false,
    });
    if (res && res.code == 10000) {
      const data = (this.state.isContract ? res.data.shop : res.data) || {}
      let thumbnailFileList = this.state.isContract ? data.contract ? data.contract.split(',') : [] : data?.map(item => item.url) || []
      thumbnailFileList = thumbnailFileList ? thumbnailFileList.map((item, index) => ({
        uid: String(index + 1),
        name: `image${index}.png`,
        status: 'done',
        url: item,
        response: { data: { url: item } },
      })) : [];

      this.setState({
        thumbnailFileList,
      })
    } else {
      message.error(res?.msg);
    }
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
      ({ file, fileList }) => {
        const list = fileList.filter((item) => item.status === 'done' || item.status === 'uploading');
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
    const { thumbnailFileList, previewOpen, previewImage, type, disabled } = this.state;

    const props = {
      rid: false,
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
      const isJpgOrPng = file.type == 'image/jpeg' || file.type == 'image/png';
      if (!isJpgOrPng) {
        message.info('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        message.info('图片大小必须小于 2MB！');
        return false;
      }
      return true;
    };

    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col>
            <h2>{this.props.shopName}-{this.state.isContract ? '合同照片' : '海报管理'}</h2>
          </Col>
          <Col>
            <Button onClick={() => history.back()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          labelCol={{
            span: 2,
          }}
          wrapperCol={{
            span: 10,
          }}
          onFinish={this.onFinish}
          initialValues={{
            remember: true,
          }}
          autoComplete="off"
        >
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>{this.state.isContract ? '合同照片' : '海报照片'}
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/guzhe/file/upload"
                  listType="picture-card"
                  fileList={thumbnailFileList}
                  onChange={this.handleUploadChange('thumbnailFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {thumbnailFileList.length < 5 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>支持上传最多六张图，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
          </Form.Item>
          <Form.Item>
            {(type != 'info') && (
              <>
                <Button
                  onClick={() => {
                    type == 'edit' ? this.getData() : history.back();
                  }}
                >
                  取消
                </Button>
                <Button className="mL15" type="primary" htmlType="submit">
                  保存
                </Button>
              </>
            )}
          </Form.Item>
        </Form>
        {/* 预览模态框 */}
        <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
          <img alt="预览" style={{ width: '100%' }} src={previewImage} />
        </Modal>
      </Spin>
    );
  }
}

export default contractPhotos;
