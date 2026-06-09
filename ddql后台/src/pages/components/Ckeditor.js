import React from 'react';
import CKEditor from 'react-ckeditor-wrapper';

export default class Ckeditor extends React.Component {
  updateContent = (value) => {
    console.log(value);
  };
  state = {
    ckeditor: undefined,
  };
  render() {
    return (
      <div>
        <CKEditor
          ref={(ckeditor) => {
            this.state.ckeditor = ckeditor;
          }}
          // value={this.state.content}
          config={{
            font_names:
              '宋体/SimSun;新宋体/NSimSun;仿宋/FangSong;楷体/KaiTi;仿宋_GB2312/FangSong_GB2312;' +
              '楷体_GB2312/KaiTi_GB2312;黑体/SimHei;华文细黑/STXihei;华文楷体/STKaiti;华文宋体/STSong;华文中宋/STZhongsong;' +
              '华文仿宋/STFangsong;华文彩云/STCaiyun;华文琥珀/STHupo;华文隶书/STLiti;华文行楷/STXingkai;华文新魏/STXinwei;' +
              '方正舒体/FZShuTi;方正姚体/FZYaoti;细明体/MingLiU;新细明体/PMingLiU;微软雅黑/Microsoft YaHei;微软正黑/Microsoft JhengHei;' +
              'Arial Black/Arial Black;',
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
                  'Print',
                  'SpellChecker',
                  'Scayt',
                ],
              },
              {
                name: 'basicstyles',
                items: ['Bold', 'Italic', 'Underline', 'Strike', '-', 'Subscript', 'Superscript'],
              },
              {
                name: 'align',
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
              {
                name: 'insert',
                items: [
                  'Image',
                  'Flash',
                  'Table',
                  'HorizontalRule',
                  'Smiley',
                  'SpecialChar',
                  'PageBreak',
                ],
              },
              { name: 'style', items: ['Font', 'FontSize'] },
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
        {/* <div onClick={()=>{
        const ckedit = this.state.ckeditor;
        const updataLocalImg = ckedit.instance.document.createElement('img');
        updataLocalImg.setAttribute('src',require('@/assets/images/loginpic.png'));
        ckedit.instance.insertElement(updataLocalImg)
      }}>
        上传文件
      </div> */}
      </div>
    );
  }
}
