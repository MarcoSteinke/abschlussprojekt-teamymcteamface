package mops.foren;

import mops.foren.domain.model.TopicId;
import mops.foren.infrastructure.persistence.dtos.ThreadDTO;
import mops.foren.infrastructure.persistence.dtos.TopicDTO;
import mops.foren.infrastructure.persistence.mapper.ThreadMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ThreadMapperTests {

    private final ThreadDTO threadDTO;

    /**
     * Constructs the test environment for ThreadMapper.
     */
    public ThreadMapperTests() {
        TopicId topicId = new TopicId(2L);

        TopicDTO topicDTO = TopicDTO.builder() // only Id is needed
                .id(topicId.getId())
                .build();

        this.threadDTO = ThreadDTO.builder()
                .id(1L)
                .topic(topicDTO)
                .title("thread title")
                .description("thread description")
                .build();
    }

    @Test
    public void testTitleIsCorrectlyMappedFromThreadDTOToModel() {
        // Act
        String title = ThreadMapper.mapThreadDtoToThread(this.threadDTO).getTitle();

        // Assert
        assertThat(title).isEqualTo(this.threadDTO.getTitle());
    }

    @Test
    public void testDescriptionIsCorrectlyMappedFromThreadDTOToModel() {
        // Act
        String description = ThreadMapper.mapThreadDtoToThread(this.threadDTO).getDescription();

        // Assert
        assertThat(description).isEqualTo(this.threadDTO.getDescription());
    }

    @Test
    public void testIdIsCorrectlyMappedFromThreadDTOToModel() {
        // Act
        Long threadId = ThreadMapper.mapThreadDtoToThread(this.threadDTO).getId().getId();

        // Assert
        assertThat(threadId).isEqualTo(this.threadDTO.getId());
    }

    @Test
    public void testTopicIdIsCorrectlyMappedFromThreadDTOToModel() {
        // Act
        TopicId topicId = ThreadMapper.mapThreadDtoToThread(this.threadDTO).getTopicId();

        // Assert
        assertThat(topicId.getId()).isEqualTo(this.threadDTO.getTopic().getId());
    }
}
