package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;

public class QuestionAddVO implements Serializable {
    private String questionName;

    private String questionType;//1000 单选题；2000多选题

    private String questionDesc;

    private String answerType;//1000	日期输入框;2000	下拉选择框;3000	文本输入框;4000	单选框;5000	字符编辑框;6000	是与否控制框;7000	数值输入框;8000	多选框;9000	文本标签

    private String defaultAnswer;


}
