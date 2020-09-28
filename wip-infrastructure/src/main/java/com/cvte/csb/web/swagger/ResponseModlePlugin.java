package com.cvte.csb.web.swagger;

import com.cvte.csb.web.swagger.annonation.ApiResponse;
import com.cvte.csb.web.swagger.annonation.ApiResponseDoc;
import com.google.common.base.Optional;
import javassist.*;
import javassist.bytecode.BadBytecode;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

import java.io.IOException;

/**
 * swagger 返回数据model处理
 *
 * @author ace
 * @create 2018/5/18.
 * liuxiaojing:修复swagger拓展注解@ApiResponseDoc在多Docket时会报错。
 */
@Component
public class ResponseModlePlugin implements OperationModelsProviderPlugin {

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }


    @Override
    public void apply(RequestMappingContext context) {
        Optional<ApiResponseDoc> annotation = context.findAnnotation(ApiResponseDoc.class);
        if (annotation.isPresent()) {
            ApiResponseDoc responseDoc = annotation.get();
            try {
                Class clazz = build(responseDoc, SwaggerDocumentUtils.captureName(context.getName()));
                context.operationModelsBuilder().addReturn(clazz);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 动态创建response 对象
     *
     * @param annotation
     * @param classFlag
     * @return
     * @throws IOException
     */
    public Class build(ApiResponseDoc annotation, String classFlag) throws CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        //从缓存中获取 --liuxiaojing 20200924
        Class clazz = this.getClassFromPool(pool, SwaggerDocumentConstant.SWAGGER_CLASS_PREFIX + classFlag);
        if (clazz != null){
            return clazz;
        }
        //创建类
        CtClass cls = pool.makeClass(SwaggerDocumentConstant.SWAGGER_CLASS_PREFIX + classFlag);
        try {
            Class data = getDataClass(annotation, classFlag);
            addClassField(pool, cls, "status", String.class.getName());
            addClassField(pool, cls, "message", String.class.getName());
            addClassField(pool, cls, "data", data.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cls.toClass();
    }

    /**
     * 创建data class
     *
     * @param annotation
     * @param classFlag
     * @return
     * @throws CannotCompileException
     * @throws NotFoundException
     */
    private Class getDataClass(ApiResponseDoc annotation, String classFlag) throws CannotCompileException, NotFoundException, IOException, BadBytecode {
        ClassPool pool = ClassPool.getDefault();
        CtClass data = pool.makeClass(SwaggerDocumentConstant.SWAGGER_CLASS_DATA_PREFIX + classFlag + SwaggerDocumentConstant.SWAGGER_CLASS_DATA_END);
        if (annotation.value() != null && annotation.value().length == 1) {
            return annotation.value()[0].value();
        }
        for (ApiResponse desc : annotation.value()) {
            addClassField(pool, data, desc.key(), desc.value().getName());
        }
        return data.toClass();
    }

    /**
     * 设置class属性
     *
     * @param pool
     * @param ctClass
     * @param field
     * @param filedType
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void addClassField(ClassPool pool, CtClass ctClass, String field, String filedType) throws NotFoundException, CannotCompileException {
        CtField param = new CtField(pool.get(filedType), field, ctClass);
        param.setModifiers(Modifier.PRIVATE);
        ctClass.addMethod(CtNewMethod.setter(SwaggerDocumentConstant.SWAGGER_CLASS_METHOD_SET + SwaggerDocumentUtils.captureName(field), param));
        ctClass.addMethod(CtNewMethod.getter(SwaggerDocumentConstant.SWAGGER_CLASS_METHOD_GET + SwaggerDocumentUtils.captureName(field), param));
        ctClass.addField(param);
    }

    /**
     * 从ClassPool中获取缓存的类
     * @param pool
     * @param classFlag
     * @return
     */
    private Class getClassFromPool(ClassPool pool, String classFlag){
        Class clazz = null;
        CtClass ctClass = pool.getOrNull(classFlag);
        if (ctClass != null) {
            try {
                return Class.forName(classFlag);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }
}
