import { getToken } from '@/utils/authority';
import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-layout';
import { useModel } from '@umijs/max';
import { Button, Form, Input, message, Spin, Tabs, Upload } from 'antd';
import { useEffect, useRef, useState } from 'react';
import CKEditor from 'react-ckeditor-wrapper';
import ArticleManagement from './components/ArticleManagement';

const { TabPane } = Tabs;

const NoticeNotice = () => {
  const formRef = useRef();
  const ckeditor = useRef();
  const [spinning, setSpinning] = useState(false);
  const [xxx, setXxx] = useState(true);
  const [imageUrl, setImageUrl] = useState();
  const [loading, setLoading] = useState(false);
  const [listss, setListss] = useState([]);
  const { setFInfo } = useModel('global');
  const [content, setContent] = useState('');

  const getData = async () => {
    setSpinning(true);
    const res = await post('/guzhe/system/basic/config');
    setSpinning(false);
    if (res && res.code === 10000) {
      const object = {};
      const listss = res.data
        .filter((item) => item.key !== 'login_page_pic')
        .map((item) => {
          object[item.key] = item.value;
          return item;
        });
      setListss(listss);
      setContent(object?.introduce);
      setImageUrl(object?.logo);

      formRef.current?.setFieldsValue(object);
      localStorage.setItem('config', JSON.stringify(object));
      setFInfo({
        version: object?.version,
        miitbeian: object?.miitbeian,
        logo: object?.logo,
        name: object?.name,
      });
    } else {
      message.error(res?.msg);
    }
  };

  useEffect(() => {
    getData();
    // 动态对齐直传按钮到 CKEditor 的图片小图标上
    const interval = setInterval(() => {
      const wrapper = document.getElementById('system-info-editor-wrapper');
      if (wrapper) {
        const imgBtn = wrapper.querySelector('.cke_button__image');
        const zxc = wrapper.querySelector('.zxc');
        if (imgBtn && zxc) {
          const btnRect = imgBtn.getBoundingClientRect();
          const wrapperRect = wrapper.getBoundingClientRect();
          if (btnRect.width > 0) {
            zxc.style.position = 'absolute';
            zxc.style.left = btnRect.left - wrapperRect.left + 'px';
            zxc.style.top = btnRect.top - wrapperRect.top + 'px';
            zxc.style.width = btnRect.width + 'px';
            zxc.style.height = btnRect.height + 'px';
            zxc.style.marginTop = '0px';
            zxc.style.marginLeft = '0px';
            zxc.style.zIndex = '999';
          }
        }
      }
    }, 500);
    return () => clearInterval(interval);
  }, []);

  const onFinish = async (res) => {
    const configData = listss.map((item) => {
      let data = res[item.key] || '';
      if (item.key === 'logo') data = imageUrl;
      if (item.key === 'introduce') data = content;
      return {
        ...item,
        value: data,
      };
    });
    const ress = await post('/guzhe/system/basic/config/update', {
      configData,
    });
    if (ress && ress.code === 10000) {
      message.success(ress.msg);
      getData();
    } else {
      message.error(ress?.msg);
    }
  };

  const resets = () => {
    getData();
    setImageUrl(undefined);
  };

  const callback = (key) => {
    if (key === '1') {
      getData();
    }
    setXxx(key === '1' ? false : true);
  };

  const beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') === -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  const handleChange = (info) => {
    if (info.file.status === 'uploading') {
      setLoading(true);
      return;
    }
    if (info.file.status === 'done') {
      console.log(info);
      setImageUrl(urlName + info.file.response.data.url);
      setLoading(false);

      message.success({ content: '上传成功', duration: 0.7 });
    }
  };

  const uploadButton = (
    <div>
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>上传</div>
    </div>
  );

  const uploadProps = (type, index) => {
    return {
      name: 'file',
      action: '/guzhe/file/upload',
      headers: { token: localStorage.getItem('token') },
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
          const url = urlName + info.file.response.data.url;
          const ele = ckeditor.current.instance.document.createElement('img');
          ele.setAttribute('src', url);

          ckeditor.current.instance.insertElement(ele);
        }
      },
    };
  };

  return (
    <div className="zxcv">
      <div className="asd">
        <PageContainer
          header={{
            title: ``,
          }}
        >
          <Spin spinning={spinning}>
            <div style={{ backgroundColor: '#f0f2f5', marginBottom: 15 }}>
              <div style={{ backgroundColor: '#fff' }}>
                <Tabs defaultActiveKey="1" onChange={callback}>
                  <TabPane tab="基础信息" key="1">
                    <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                      <div style={{ backgroundColor: '#fff' }}>
                        <div
                          style={{
                            backgroundColor: '#f0f2f5',
                            paddingBottom: 15,
                          }}
                        >
                          <div style={{ padding: 24, backgroundColor: '#fff' }}>
                            <span style={{ fontSize: 18 }}>
                              <b>基础信息</b>
                            </span>
                            <Form
                              ref={formRef}
                              onFinish={onFinish}
                              labelCol={{ span: 3 }}
                              wrapperCol={{ span: 10 }}
                              style={{ marginTop: 25 }}
                            >
                              <Form.Item
                                label={
                                  <span>
                                    <span style={{ color: 'red' }}>*</span>
                                    系统logo
                                  </span>
                                }
                              >
                                <Form.Item
                                  name="logo"
                                  noStyle
                                  rules={[
                                    { required: true, message: '请上传!' },
                                  ]}
                                >
                                  <Upload
                                    name="file"
                                    listType="picture-card"
                                    className="avatar-uploader"
                                    showUploadList={false}
                                    action="/guzhe/file/upload"
                                    headers={{
                                      token: localStorage.getItem('token'),
                                    }}
                                    beforeUpload={beforeUpload}
                                    onChange={handleChange}
                                  >
                                    {imageUrl ? (
                                      <img
                                        src={imageUrl}
                                        alt="avatar"
                                        style={{
                                          width: '100%',
                                          height: '100%',
                                          objectFit: 'cover',
                                        }}
                                      />
                                    ) : (
                                      uploadButton
                                    )}
                                  </Upload>
                                </Form.Item>
                                <div style={{ color: '#ccc' }}>
                                  建议尺寸 476*75px
                                </div>
                              </Form.Item>

                              <Form.Item label="系统名称" name="name">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="单位名称" name="org_name">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="联系地址" name="address">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="联系电话" name="phone">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="邮箱" name="email">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="版权信息" name="version">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item label="备案信息" name="miitbeian">
                                <Input placeholder="请输入" />
                              </Form.Item>
                              <Form.Item
                                label={
                                  <span>
                                    <span style={{ color: 'red' }}>*</span>
                                    平台介绍
                                  </span>
                                }
                                rules={[{ required: true, message: '请输入!' }]}
                              >
                                <div
                                  id="system-info-editor-wrapper"
                                  style={{ position: 'relative' }}
                                >
                                  <Upload
                                    className="system-settings-upload"
                                    showUploadList={false}
                                    accept={'image/*'}
                                    headers={{ token: getToken() }}
                                    {...uploadProps(1)}
                                  >
                                    <div className="zxc" />
                                  </Upload>
                                  <CKEditor
                                    ref={ckeditor}
                                    value={content}
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
                                        {
                                          name: 'links',
                                          items: ['Link', 'Unlink'],
                                        },
                                        {
                                          name: 'insert',
                                          items: ['Image', 'Table'],
                                        },
                                        {
                                          name: 'styles',
                                          items: ['Font', 'FontSize'],
                                        },
                                        {
                                          name: 'colors',
                                          items: ['TextColor', 'BGColor'],
                                        },
                                        { name: 'tools', items: ['Maximize'] },
                                      ],
                                      extraPlugins: 'placeholder',
                                      height: 250,
                                      removeDialogTabs:
                                        'image:advanced;link:advanced;image:Link',
                                    }}
                                    onChange={(value) => {
                                      setContent(value);
                                    }}
                                  />
                                </div>
                              </Form.Item>

                              <Form.Item wrapperCol={{ offset: 3, span: 10 }}>
                                <Button type="primary" htmlType="submit">
                                  保存
                                </Button>
                                <Button
                                  style={{ marginLeft: 15 }}
                                  onClick={resets}
                                >
                                  重置
                                </Button>
                              </Form.Item>
                            </Form>
                          </div>
                        </div>
                      </div>
                    </div>
                  </TabPane>
                  <TabPane tab="文章管理" key="2">
                    <div style={{ backgroundColor: '#f0f2f5', padding: 24 }}>
                      <div style={{ backgroundColor: '#fff' }}>
                        {xxx && <ArticleManagement />}
                      </div>
                    </div>
                  </TabPane>
                </Tabs>
              </div>
            </div>
          </Spin>
        </PageContainer>
      </div>
    </div>
  );
};

export default NoticeNotice;
