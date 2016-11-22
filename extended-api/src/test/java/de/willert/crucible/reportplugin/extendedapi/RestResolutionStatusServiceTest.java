package de.willert.crucible.reportplugin.extendedapi;

import com.atlassian.crucible.spi.PermId;
import com.atlassian.crucible.spi.data.CommentData;
import com.atlassian.crucible.spi.data.CommentResolutionData;
import com.atlassian.crucible.spi.data.CommentResolutionStatus;
import com.atlassian.crucible.spi.data.UserData;
import com.atlassian.crucible.spi.services.NotFoundException;
import com.atlassian.crucible.spi.services.NotPermittedException;
import com.atlassian.crucible.spi.services.ReviewService;
import de.willert.crucible.reportplugin.extendedapi.util.RESTTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.Date;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by czoeller on 24.10.16.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class RestResolutionStatusServiceTest extends RESTTest {

    @Mock
    private ReviewService reviewService;
    private RestResolutionStatusService restResolutionStatusService;

    private CommentResolutionData resolutionData = new CommentResolutionData(CommentResolutionStatus.UNRESOLVED, new UserData("hello", "hello", ""), new Date());

    @Before
    public void setUpChild() {
        this.restResolutionStatusService = new RestResolutionStatusService(reviewService);
    }

    @Test
    public void isResolvedCallsServiceWithId() throws Exception {
        PermId<CommentData> permId = new PermId<>("2");
        when(reviewService.getCommentResolution( any(PermId.class) )).thenReturn( resolutionData );

        restResolutionStatusService.resolutionStatus("2");

        verify(reviewService, times(1)).getCommentResolution( eq( permId ) );
    }

    @Test
    public void rejectsInvalidEmptyCId() throws Exception {
        final Response response = restResolutionStatusService.resolutionStatus("");
        assertResponseContains((String) response.getEntity(), RestResolutionStatusService.ERROR_INVALID_ID);
    }

    @Test
    public void rejectsInvalidCId() throws Exception {
        final Response response = restResolutionStatusService.resolutionStatus("helloworld");
        assertResponseContains((String) response.getEntity(), RestResolutionStatusService.ERROR_INVALID_ID);
    }

    @Test
    public void rejectsUnknowCId() throws Exception {
        when(reviewService.getCommentResolution( any(PermId.class) )).thenThrow( new NotFoundException("") );

        final Response response = restResolutionStatusService.resolutionStatus("2");

        assertResponseContains((String) response.getEntity(), RestResolutionStatusService.ERROR_UNKNOWN_ID);
    }

    @Test
    public void notPermitted() throws Exception {
        when(reviewService.getCommentResolution( any(PermId.class) )).thenThrow( new NotPermittedException("") );

        final Response response = restResolutionStatusService.resolutionStatus("2");

        assertResponseContains((String) response.getEntity(), RestResolutionStatusService.ERROR_NOT_PERMITTED);
    }

}