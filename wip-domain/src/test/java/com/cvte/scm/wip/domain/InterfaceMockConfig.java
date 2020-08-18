package com.cvte.scm.wip.domain;

import lombok.SneakyThrows;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/17 16:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Configuration
public class InterfaceMockConfig implements BeanDefinitionRegistryPostProcessor {

    private Set<Class> candidates = new HashSet<>();

    {
        try {
            this.findNoImplInterfaces("com/cvte/scm/wip/domain");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for (Class candidate : candidates) {
            configurableListableBeanFactory.registerResolvableDependency(candidate, Mockito.mock(candidate));
        }
    }

    private void findNoImplInterfaces(String basePackage) throws IOException, ClassNotFoundException {
        // 接口-实现类列表 字典, 用于排除domain包内有实现类的接口(无需Mock)
        Map<Class, Set<Class>> interfaceImplMap = new HashMap<>();
        List<MetadataReader> metadataReaderList = new ArrayList<>();

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        String packageSearchPath = resolveBasePackage(basePackage);
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                metadataReaderList.add(metadataReader);
            }
        }
        for (MetadataReader metadataReader : metadataReaderList) {
            produceEntry(metadataReader, interfaceImplMap);
        }
        for (MetadataReader metadataReader : metadataReaderList) {
            if (isCandidate(metadataReader, interfaceImplMap)) {
                this.candidates.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
            }
        }
    }

    private String resolveBasePackage(String basePackage) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/" + "**/*.class";
    }

    /**
     *
     * @since 2020/8/17 7:20 下午
     * @author xueyuting
     */
    @SneakyThrows
    private void produceEntry(MetadataReader metadataReader, Map<Class, Set<Class>> interfaceImplMap) {
        Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
        Class[] implInterfaces = ClassUtils.getAllInterfaces(c);
        if (null != implInterfaces && implInterfaces.length > 0) {
            for (Class implInterface : implInterfaces) {
                Set<Class> derivedClasses = interfaceImplMap.computeIfAbsent(implInterface, k -> new HashSet<>());
                derivedClasses.add(c);
            }
        }
    }

    /**
     * 筛选出没有实现类的接口
     * @since 2020/8/17 7:16 下午
     * @author xueyuting
     */
    @SneakyThrows
    private boolean isCandidate(MetadataReader metadataReader, Map<Class, Set<Class>> interfaceImplMap) {
        Class c = Class.forName(metadataReader.getClassMetadata().getClassName());
        // 排除包内有实现类的接口
        Set<Class> derivedClass = interfaceImplMap.get(c);
        return c.isInterface() && (derivedClass == null || !derivedClass.isEmpty());
    }

}
