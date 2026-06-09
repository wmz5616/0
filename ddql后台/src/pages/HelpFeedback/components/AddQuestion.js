import React from 'react';
import { Modal, Form, Input, Select, Radio, DatePicker, Upload, message, InputNumber } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import CKEditor from 'react-ckeditor-wrapper';
const { TextArea } = Input;
const { Option } = Select;
import moment from 'moment';
import { history, connect } from 'umi';
const { RangePicker } = DatePicker;
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 18 },
};
class App extends React.Component {
  formRef = React.createRef();
  state = {
    loading: false,
  };

  componentDidMount() {
    this.getData();
  }

  getData = () => {
    this.setState(
      {
        spinning: true,
      },
      () => {
        //列表
        const { edit } = this.props;
        //问题类型
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            limit: 999,
          },
          url: `/api/admin/helper/qa/type/lists`,
          method: 'GET',
          myData: (res) => {
            console.log(res);
            this.setState({
              spinning: false,
            });
            if (res && res.code === 200) {
              console.log(res.data.lists);
              this.setState({
                listss: res.data.lists,
                total: res.data.total,
              });
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });

        if (edit) {
          this.setState({
            content: edit.content,
          });
          this.formRef.current.setFieldsValue({
            title: edit.title,
            sort: edit.sort,
            publish: edit.publish,
            content: edit.content,
            qa_types: edit.qa_types.map((res) => res.qa_type.id),
          });
        }
      },
    );
  };

  handleOk = () => {
    const { handleOk, dispatch, getData, add, edit } = this.props;
    this.formRef.current.validateFields().then((values) => {
      if (add) {
        //新建
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            title: values.title, //名称
            sort: values.sort, //
            content: this.state.content, //
            qa_types: values.qa_types.join(','), //
            publish: values.publish,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/helper/qa/add`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            }
          },
        });
      } else {
        //编辑
        dispatch({
          type: 'myModel/getSetData',
          payload: {
            id: edit.id,
            title: values.title, //名称
            sort: values.sort, //
            content: this.state.content, //
            qa_types: values.qa_types.join(','), //
            publish: values.publish,
          },
          // dataName: 'developerListData',
          method: 'POST',
          url: `/api/admin/helper/qa/update`,
          myData: (res) => {
            if (res.code === 200) {
              message.success(res.message);
              handleOk();
              getData();
            }
          },
        });
      }
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  handleChange = (info) => {
    if (info.file.status === 'uploading') {
      this.setState({ loading: true });
      return;
    }
    if (info.file.status === 'done') {
      console.log(info);
      this.setState({
        addUrl: info.file.response.data.uri,
      });

      message.success({ content: '上传成功', duration: 0.7 });
      // Get this url from response in real world.
      this.getBase64(info.file.originFileObj, (imageUrl) =>
        this.setState({
          imageUrl,
          uditUrl: imageUrl,
          loading: false,
        }),
      );
    }
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  // handleChanges(value) {
  //   this.setState({
  //     cc:sizeof(value)
  //   },()=>{
  //     console.log(this.state.cc)
  //   })
  // }
  updateContent = (value, index) => {
    console.log(value);
    this.setState({
      content: value,
    });
  };

  render() {
    const { add } = this.props;

    const { loading, imageUrl, listss = [] } = this.state;
    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

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
      <>
        <Modal
          title={add ? '新增问题' : '编辑问题'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item label="类型" name="qa_types"  rules={[{ required: true }]}>
              <Select mode="multiple" allowClear showSearch placeholder="请选择"optionFilterProp="label">
                {listss.map((res) => {
                  return (
                    <Option value={res.id} key={res.id} label={`${res.id}${res.title}`} >
                      {res.title}
                    </Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item label="标题" name="title" rules={[{ required: true }]}>
              <Input />
            </Form.Item>

            <Form.Item
              label="立即发布"
              name="publish"
              rules={[{ required: true }]}
              initialValue={1}
            >
              <Radio.Group>
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>

            


            <Form.Item
            label={<span><span style={{color:'red'}}>*</span>排序</span>}
          >
            <Form.Item
              name="sort"
              noStyle
              rules={[{ required: true, message: '请输入!' }]}
              initialValue={0}
            >
              <Input />
            </Form.Item>
            <div style={{color:'#ccc'}}>数值越大，展示越靠前</div>
          </Form.Item>








            <Form.Item label={<span><span style={{color:'red'}}>*</span>正文</span>} rules={[{ required: true, message: '请输入!' }]}>
              <div style={{ position: 'relative' }}>
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
                    // uploadUrl: '/home/media/upload',
                    removeDialogTabs: 'image:advanced;link:advanced',
                  }}
                  onChange={this.updateContent}
                />
              </div>
            </Form.Item>
          </Form>
        </Modal>
      </>
    );
  }
}

export default connect()(App);
